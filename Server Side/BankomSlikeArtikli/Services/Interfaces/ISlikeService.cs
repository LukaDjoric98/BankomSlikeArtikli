using BankomSlikeArtikli.Models.ResponseDto;
using Microsoft.AspNetCore.Http;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Services.Interfaces
{
    public interface ISlikeService
    {
        // Definisemo metodu za cuvanje slika u skladiste i u bazu
        Task<bool> UploadFile(IFormFile file, int artikalId);
        // Definisemo metodu za dobijanje liste svih slika iz baze
        Task<IEnumerable<SlikaDto>> GetAllSlikeListAsync();
        // Definisemo metodu za dobijanje slike prema njenom id
        Task<SlikaDto> GetSlikaByIdAsync(int id);
        // Definisemo metodu za proveru da li postoji slika naspram id artikla
        Task<bool> ExistsSlikaByArtikalIdAsync(int artikalId);
        // Definisemo metodu za brisanje slike naspram id artikla
        Task<bool> DeleteSlikaByArtikalIdAsync(int artikalId);
    }
}
