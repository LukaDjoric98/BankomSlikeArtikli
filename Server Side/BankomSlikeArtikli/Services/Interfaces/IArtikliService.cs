using BankomSlikeArtikli.Models.ResponseDto;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Services.Interfaces
{
    public interface IArtikliService
    {
        // Definisemo metodu za vracanje svih artikala
        Task<IEnumerable<ArtikalDto>> GetAllArtikalAsync();
    }
}
