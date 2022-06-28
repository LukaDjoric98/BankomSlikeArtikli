using BankomSlikeArtikli.Services.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.IO;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Controllers
{
    [Route("api/slike")]
    [ApiController]
    [ApiConventionType(typeof(DefaultApiConventions))]
    public class SlikeController : ControllerBase
    {
        private readonly ISlikeService _slikeService;

        public SlikeController(ISlikeService slikeService)
        {
            _slikeService = slikeService ?? throw new ArgumentNullException(nameof(slikeService));
        }

        [HttpGet(Name = "GetAllSlikeList")]
        public async Task<IActionResult> GetAllSlikeList()
        {
            // U promenljivu cuvamo listu slika
            var res = await _slikeService.GetAllSlikeListAsync();

            // Ukoliko je lista slika prazna dobijamo odgovor da je los zahtev
            if (res == null)
            {
                return BadRequest();
            }

            // Vracamo odgovor Ok sa listom slika
            return Ok(res);
        }

        [HttpGet("{id}", Name = "GetSlikaById")]
        public async Task<IActionResult> GetSlikaById([FromRoute] int id)
        {
            // Dobijamo objekat slike iz baze
            var slika = await _slikeService.GetSlikaByIdAsync(id);

            // Ukoliko ne postoji objekat vracamo da nije pronadjena slika
            if (slika == null)
            {
                return NotFound();
            }

            // Ukoliko ne postoji file sa putanjom iz objekta slike vracamo da nije pronadjena slika
            if (!System.IO.File.Exists(slika.Put))
            {
                return NotFound();
            }

            // Citamo sliku sa putanje u bajtove
            var bytes = await System.IO.File.ReadAllBytesAsync(slika.Put);

            // Vracamo sliku kao bajtove
            return File(bytes, "text/plain", Path.GetFileName(slika.Put));
        }
        
        [HttpPost("{artikalId}", Name = "UploadFile")]
        public async Task<IActionResult> UploadFile([FromForm] IFormFile file, [FromRoute] int artikalId)
        {
            // Proveravamo da li slika postoji za zadati artikal
            var check = await _slikeService.ExistsSlikaByArtikalIdAsync(artikalId);

            if (check)
            {
                // ukoliko postoji brisemo sliku
                var del = await _slikeService.DeleteSlikaByArtikalIdAsync(artikalId);
                
                if (del)
                {
                    // Ukoliko ne uspemo da obrisemo vracamo da je los zahtev
                    return BadRequest();
                }
                
            }

            // Cuvamo sliku na lokaciju i u bazu
            var res = await _slikeService.UploadFile(file, artikalId);
            
            
            if (!res)
            {
                // Ukoliko ne uspemo da sacuvamo vracamo da je los zahtev
                return BadRequest();
            }

            // Ukoliko je sve dobro vracamo odgovor Ok
            return Ok();
        }
    }
}
