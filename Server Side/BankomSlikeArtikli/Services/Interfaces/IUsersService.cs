using BankomSlikeArtikli.Models.RequestDto;
using BankomSlikeArtikli.Models.ResponseDto;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Services.Interfaces
{
    public interface IUsersService
    {
        // Definisemo metodu za proveru korisnika preko korisnickog imena
        Task<bool> UserExistsByNameAsync(string username);
        // Definisemo metodu za upisivanje korisnika
        Task<int> InsertUserAsync(UserInputDto inputDto);
        // Definisemo metodu za dobijanje liste svih korisnika
        Task<IEnumerable<UserDto>> GetAllUsersAsync(string username);
        // Definisemo metodu za dobijanje korisnika preko korisnikog imena
        Task<UserDto> GetUserByUsernameAsync(UserInputDto inputDto, int flag);
        // Definisemo metodu za menjanje korisnika
        Task<bool> ChangeUserAsync(UserInputDto inputDto);
        // Definisemo metodu za brisanje korisnika
        Task<bool> DeleteUserAsync(int id);

    }
}
