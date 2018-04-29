using System;
using System.Net;

namespace RecommendationSystemsClient.Services
{
    public static class ValidationService
    {
        public static bool ValidateIpAddress(string ip)
        {
            try
            {
                var result = IPAddress.TryParse(ip, out var _);
                return result;
            }
            catch (Exception)
            {
                return false;
            }
        }

        public static bool ValidateInt(string port)
        {
            try
            {
                var result = int.TryParse(port, out var _);
                return result;
            }
            catch (Exception)
            {
                return false;
            }
        }
    }
}