using BankomSlikeArtikli.Services.Interfaces;
using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Configuration;
using System;

namespace BankomSlikeArtikli.Services
{
    public abstract class BaseService
    {
        protected readonly IConfiguration _configuration;
        protected BaseService(IConfiguration configuration)
        {
            _configuration = configuration ?? throw new ArgumentNullException(nameof(configuration));
        }

        protected SqlConnection ConnectionWrite
        {
            get
            {
                return new SqlConnection(_configuration.GetConnectionString("DbConnectionString"));
            }
        }

        protected SqlConnection ConnectionRead
        {
            get
            {
                return new SqlConnection(_configuration.GetConnectionString("DbConnectionString"));
            }
        }

        protected String StorageLocation
        {
            get
            {
                return _configuration.GetConnectionString("StorageLocation");
            }
        }
    }
}
