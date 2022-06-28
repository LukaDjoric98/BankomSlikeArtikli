namespace BankomSlikeArtikli.Models.ResponseDto
{
    public class UserDto
    {
        public int Id { get; set; }
        public string Username { get; set; }
        public string Password { get; set; }
        public string Salt { get; set; }
        public string Rola { get; set; }
    }
}
