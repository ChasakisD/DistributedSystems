using System;
using Newtonsoft.Json;

namespace RecommendationSystemsClient.Models
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