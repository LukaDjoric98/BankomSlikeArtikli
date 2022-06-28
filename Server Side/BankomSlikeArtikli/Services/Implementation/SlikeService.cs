using BankomSlikeArtikli.Models.ResponseDto;
using BankomSlikeArtikli.Services.Interfaces;
using Dapper;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Services.Implementation
{
    public class SlikeService : BaseService, ISlikeService
    {
        public SlikeService(IConfiguration configuration) : base(configuration)
        {
        }

        public async Task<bool> UploadFile(IFormFile file, int artikalId)
        {
            // Proveravamo da li je file prazan
            if (file != null && file.Length > 0)
            {
                // Definisemo lokaciju za cuvanje slika
                var webRoot = StorageLocation;
                DirectoryInfo di = new DirectoryInfo(webRoot);
                FileInfo[] files = di.GetFiles();

                // Proveravamo da li postoji file sa vec postojecim imenom
                var fileExist = files.Any(x => x.Name == file.FileName);

                // Ukoliko ne postoji sa istim nazivom mozemo dalje
                if (!fileExist)
                {
                    // Dobijamo putanju slike
                    var pathWithFolderName = Path.Combine(webRoot, file.FileName);

                    // Kopiramo sliku iz memorije na putanju slike
                    using (var fileStream = new FileStream(pathWithFolderName, FileMode.Create))
                    {
                        await file.CopyToAsync(fileStream);
                    }

                    var query = @"INSERT INTO [Slike]
                                               ([Put]
                                               ,[Naziv]
                                               ,[ArtikalId])
                                         VALUES
                                               (@Put
                                               ,@Naziv
                                               ,@ArtikalId)";

                    // Definisanje dinamickih parametra
                    DynamicParameters parameters = new();
                    parameters.Add("Put", pathWithFolderName);
                    parameters.Add("Naziv", file.FileName);
                    parameters.Add("ArtikalId", artikalId);

                    using var cnn = ConnectionRead;

                    var result = await cnn.ExecuteAsync(query, parameters, commandType: CommandType.Text);
                    
                    return result > 0;
                }
            }

            return false;
        }

        public async Task<IEnumerable<SlikaDto>> GetAllSlikeListAsync()
        {
            var query = @"SELECT [Id]
                                ,[Put]
                                ,[Naziv]
                                ,[ArtikalId]
                            FROM [Slike]";

            using var cnn = ConnectionRead;

            var result = await cnn.QueryAsync<SlikaDto>(query, commandType: CommandType.Text);

            return result;
        }

        public async Task<SlikaDto> GetSlikaByIdAsync(int id)
        {
            var query = @"SELECT * FROM Slike WHERE Id = @Id";

            using var cnn = ConnectionRead;

            // Kad definisemo da trazimo po odredjenom id, moramo i parametar da prosledimo u nas upit
            var result = await cnn.QueryFirstOrDefaultAsync<SlikaDto>(query, new { id }, commandType: CommandType.Text);

            return result;
        }

        public async Task<bool> ExistsSlikaByArtikalIdAsync(int artikalId)
        {
            var query = @"SELECT * FROM Slike WHERE ArtikalId = @artikalId";

            using var cnn = ConnectionRead;

            var result = await cnn.QueryFirstOrDefaultAsync<bool>(query, new { artikalId }, commandType: CommandType.Text);

            return result;
        }

        public async Task<bool> DeleteSlikaByArtikalIdAsync(int artikalId)
        {
            // Prvo treba da pokupimo putanju na kojoj se nalazi slika
            var query = @"SELECT Put FROM Slike WHERE ArtikalId = @artikalId";

            using var cnn = ConnectionRead;

            var result = await cnn.QueryFirstOrDefaultAsync<string>(query, new { artikalId }, commandType: CommandType.Text);

            var webRoot = StorageLocation;
            DirectoryInfo di = new DirectoryInfo(webRoot);
            FileInfo[] files = di.GetFiles();

            // Zatim proveravamo da li postoji fajl sa istim imenom kao nasa slika
            var fileExist = files.Any(x => x.FullName == result);
            if (fileExist)
            {
                // Ukoliko postoji brisemo sliku na toj putanji
                File.Delete(result);
            }
            
            // Potom trebamo da obrisemo i taj objekat koji je vezan za obrisanu sliku
            var queryDelete = @"DELETE FROM Slike WHERE ArtikalId = @artikalId";

            using var cnnDelete = ConnectionRead;

            var resultDelete = await cnn.QueryFirstOrDefaultAsync<bool>(queryDelete, new { artikalId }, commandType: CommandType.Text);

            return resultDelete;
        }


    }
}
