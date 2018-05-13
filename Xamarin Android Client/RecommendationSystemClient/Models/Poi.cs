using System;
using Newtonsoft.Json;

namespace RecommendationSystemClient.Models
{
    [Serializable]
    public class Poi
    {
        [JsonProperty(PropertyName = "id")]
        public int Id { get; set; }

        [JsonProperty(PropertyName = "name")]
        public string Name { get; set; }
    }
}