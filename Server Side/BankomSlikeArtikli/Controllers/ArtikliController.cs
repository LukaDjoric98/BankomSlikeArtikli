using BankomSlikeArtikli.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Controllers
{
    [Route("api/artikli")]
    [ApiController]
    [ApiConventionType(typeof(DefaultApiConventions))]
    public class ArtikliController : ControllerBase
    {
        // Ovde definisemo koji interfejs metoda koristimo
        private readonly IArtikliService _artikliService;

        // Da bi mogli da koristimo date metode moramo da definisemo taj interfejs u samom konstruktoru
        public ArtikliController(IArtikliService artikliService)
        {
            _artikliService = artikliService ?? throw new ArgumentNullException(nameof(artikliService));
        }

        // U [] definisemo koji je HTTP zahtev u pitanju, putanju ukoliko postoji i ime metode u kontroleru
        [HttpGet(Name = "GetAllArtikli")]
        public async Task<IActionResult> GetAllArtikli()
        {
            // Pozivamo metodu GetAllArtikalAsync da bi dobili listu svih artikala i skladistimo u promenljivoj
            var result = await _artikliService.GetAllArtikalAsync();

            // Vracamo rezultat liste svih artikala sa odgovorom Ok
            return Ok(result);
        }
    }
}
