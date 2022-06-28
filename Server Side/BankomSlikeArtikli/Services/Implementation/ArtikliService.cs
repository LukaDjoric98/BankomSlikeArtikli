using BankomSlikeArtikli.Models.ResponseDto;
using BankomSlikeArtikli.Services.Interfaces;
using Dapper;
using Microsoft.Extensions.Configuration;
using System.Collections.Generic;
using System.Data;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Services.Implementation
{
    // Definisemo koje sve klase/interfejse nasledjuje nasa klasa
    public class ArtikliService : BaseService, IArtikliService
    {
        // U kontruktoru definisemo da koristimo metodu configuration iz BaseService koji smo nasledili 
        public ArtikliService(IConfiguration configuration) : base(configuration)
        {
        }

        // Definisemo nasu metodu i koji tip podataka vraca, lista artikala
        public async Task<IEnumerable<ArtikalDto>> GetAllArtikalAsync()
        {
            // Query koristimo kao promenljivu u kojoj skladistimo upit koji zelimo da izvrsimo
            var query = @"SELECT Id
                                ,Naziv
                           FROM ArtikliZaSlike";

            // Ovako otvaramo konekciju sa bazom, konekcioni stringovi su iz BaseService
            using var cnn = ConnectionRead;

            // Ovde promenljivu result popunjavamo rezultatom nakon sto izvrsimo upit koji smo prethodno definisali
            var result = await cnn.QueryAsync<ArtikalDto>(query, commandType: CommandType.Text);

            // Vracamo listu atikala kao promenljivu
            return result;
        }
    }
}
