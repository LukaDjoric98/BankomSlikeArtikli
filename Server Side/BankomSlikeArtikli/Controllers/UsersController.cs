using BankomSlikeArtikli.Models.RequestDto;
using BankomSlikeArtikli.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Threading.Tasks;

namespace BankomSlikeArtikli.Controllers
{
    [Route("api/users")]
    [ApiController]
    [ApiConventionType(typeof(DefaultApiConventions))]
    public class UsersController : ControllerBase
    {
        private readonly IUsersService _usersService;

        public UsersController(IUsersService usersService)
        {
            _usersService = usersService ?? throw new ArgumentNullException(nameof(usersService));
        }

        [HttpGet("{username}",Name = "GetAllUsers")]
        public async Task<IActionResult> GetAllUsers([FromRoute] string username)
        {
            // U promenljivu cuvamo listu korisnika
            var result = await _usersService.GetAllUsersAsync(username);

            // Ukoliko je lista korisnika prazna dobijamo odgovor da je los zahtev
            if (result == null)
            {
                return NotFound();
            }

            // Vracamo odgovor Ok sa listom korisnika
            return Ok(result);
        }

        [HttpPost("register",Name = "CreateUser")]
        public async Task<IActionResult> CreateUser([FromBody] UserInputDto inputDto)
        {
            // Proveravamo da li postoji vec korisnik sa datim korisnickim imenom
            var provera = await _usersService.UserExistsByNameAsync(inputDto.Username);

            // Ukoliko postoji vraticemo da nije uspesna registracija
            if (provera)
            {
                return BadRequest();
            }

            // Upisujemo u bazu novog korisnika
            await _usersService.InsertUserAsync(inputDto);

            // Vracamo odogovor Ok
            return Ok();
        }

        [HttpPost("login", Name = "LoginUser")]
        public async Task<IActionResult> LoginUser([FromBody] UserInputDto inputDto)
        {
            // Proveravamo da li postoji vec korisnik sa datim korisnickim imenom
            var provera = await _usersService.UserExistsByNameAsync(inputDto.Username);

            // Ukoliko postoji vraticemo da nije uspesno logovanje
            if (!provera)
            {
                return BadRequest();

            }

            // Vracamo korisnika po njegovom korisnickom imenu
            var result = await _usersService.GetUserByUsernameAsync(inputDto, 1);

            // Ukoliko je prazan rezultat vracamo da nije uspesno logovanje
            if (result == null)
            {
                return BadRequest();

            }

            // Vracamo odgovor Ok sa objektom korisnika koji cuvamo na klijentskoj strani
            return Ok(result);
        }

        [HttpPut(Name = "ChangeUser")]
        public async Task<IActionResult> ChangeUser([FromBody] UserInputDto inputDto)
        {
            // Uzimamo korisnika preko njegovog korisnickog imena
            var korisnik = await _usersService.GetUserByUsernameAsync(inputDto, 0);

            // Ukoliko korisnik ne postoji
            if(korisnik == null)
            {
                // Upisujemo novog korisnika
                await _usersService.InsertUserAsync(inputDto);
                // Brisemo starog korisnika
                await _usersService.DeleteUserAsync((int)inputDto.Id);
                return Ok();
            }

            // Menjamo korisnika u bazi
            var result = await _usersService.ChangeUserAsync(inputDto);

            // Ukoliko neuspelo promenimo korisnika vracamo da nije uspelo
            if(result)
            {
                return BadRequest();
            }

            // Vracamo odgovor Ok
            return Ok();
        }

        [HttpDelete("{id}", Name = "DeleteUser")]
        public async Task<IActionResult> DeleteUser([FromRoute] int id)
        {
            // Brisemo korisnika prema njegovom identifikacionom broju
            var result = await _usersService.DeleteUserAsync(id);

            // Ukoliko nije uspesno vraticemo da nije uspeno brisanje
            if (result)
            {
                return BadRequest();
            }

            // Vracamo odgovor Ok
            return Ok();
        }

    }
}
