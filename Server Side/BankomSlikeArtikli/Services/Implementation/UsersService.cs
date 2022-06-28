using BankomSlikeArtikli.Helpers;
using BankomSlikeArtikli.Models.RequestDto;
using BankomSlikeArtikli.Models.ResponseDto;
using BankomSlikeArtikli.Services.Interfaces;
using Dapper;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Data;
using System.Text;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Services.Implementation
{
    public class UsersService : BaseService, IUsersService
    {
        public UsersService(IConfiguration configuration) : base(configuration)
        {
        }

        public async Task<IEnumerable<UserDto>> GetAllUsersAsync(string username)
        {
            var query = @"SELECT * FROM Korisnici WHERE Username <> @username";

            using var cnn = ConnectionRead;

            var result = await cnn.QueryAsync<UserDto>(query, new { username }, commandType: CommandType.Text);

            return result;
        }

        public async Task<bool> UserExistsByNameAsync(string username)
        {
            var query = @"SELECT Id FROM Korisnici WHERE Username = @Username";

            var parameters = new DynamicParameters();

            parameters.Add("@Username", username);

            using var cnn = ConnectionRead;

            var result = await cnn.ExecuteScalarAsync<bool>(query, parameters, commandType: CommandType.Text);

            return result;
        }

        public async Task<int> InsertUserAsync(UserInputDto inputDto)
        {
            var query = @"INSERT INTO [Korisnici] (
	                                               [Username]
	                                               ,[Password]
	                                               ,[Salt]
                                                   ,[Rola]
	                                               )
                                            VALUES (
	                                               @Username
	                                               ,@Password
	                                               ,@Salt
                                                   ,@Rola
	                                               )
                                                        
                                      SELECT SCOPE_IDENTITY();";

            var parameters = new DynamicParameters();

            // Koristimo pomocnu metodu ga generisanje Salta koji koristimo za hesiranje lozinki
            var salt = Convert.ToBase64String(SaltPassword.GetRandomSalt(16));

            // Hesiramo lozinku
            var password = Convert.ToBase64String(SaltPassword.SaltHashPassword(
                    Encoding.ASCII.GetBytes(inputDto.Password),
                    Convert.FromBase64String(salt)
                ));

            parameters.Add("@Username", inputDto.Username);
            parameters.Add("@Salt", salt);
            parameters.Add("@Password", password);
            if (inputDto.Rola != null)
                parameters.Add("@Rola", inputDto.Rola);
            else
                parameters.Add("@Rola", "Korisnik");

            using var cnn = ConnectionWrite;

            var result = await cnn.ExecuteScalarAsync<int>(query, parameters, commandType: CommandType.Text);

            return result;
        }

        public async Task<UserDto> GetUserByUsernameAsync(UserInputDto inputDto, int flag)
        {
            var query = @"SELECT * FROM Korisnici WHERE Username = @username";

            using var cnn = ConnectionRead;

            var result = await cnn.QueryFirstOrDefaultAsync<UserDto>(query, new { inputDto.Username }, commandType: CommandType.Text);

            if (flag == 1)
            {
                var client_post_hash_password = Convert.ToBase64String(
                    SaltPassword.SaltHashPassword(
                        Encoding.ASCII.GetBytes(inputDto.Password),
                        Convert.FromBase64String(result.Salt)));

                if (client_post_hash_password.Equals(result.Password))
                    return result;
                else
                    return null;
            } 
            
            return result;
            
        }

        public async Task<bool> ChangeUserAsync(UserInputDto inputDto)
        {
            var query = @"UPDATE Korisnici
                           SET [Username] = @Username
	                          ,[Rola] = @Rola";

            var where = "WHERE Id = @Id";

            var parameters = new DynamicParameters();
            parameters.Add(name: "@Id", inputDto.Id);
            parameters.Add("Username", inputDto.Username);
            parameters.Add("Rola", inputDto.Rola);

            if (inputDto.Password != null)
            {
                var salt = Convert.ToBase64String(SaltPassword.GetRandomSalt(16));
                var password = Convert.ToBase64String(SaltPassword.SaltHashPassword(
                        Encoding.ASCII.GetBytes(inputDto.Password),
                        Convert.FromBase64String(salt)
                    ));
                query = $"{query},[Salt] = @Salt ,[Password] = @Password";
                parameters.Add("@Salt", salt);
                parameters.Add("@Password", password);

            }

            query = $"{query} {where}";

            using var cnn = ConnectionWrite;

            var result = await cnn.ExecuteScalarAsync<bool>(query, parameters, commandType: CommandType.Text);

            return result;
        }

        public async Task<bool> DeleteUserAsync(int id)
        {
            var query = @"DELETE FROM Korisnici WHERE Id = @id";

            using var cnn = ConnectionWrite;

            var result = await cnn.ExecuteScalarAsync<bool>(query, new { id }, commandType: CommandType.Text);

            return result;
        }
    }
}
