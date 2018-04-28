using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace RecommendationSystemsClient.Models
{
    public enum MessageType
    {
        HelloWorld,
        TransferMatrices,
        CalculateX,
        CalculateY,
        XCalculated,
        YCalculated,
        AskRecommendation,
        ReplyRecommendation
    }

    [Serializable]
    public class CommunicationMessage
    {
        [JsonProperty(PropertyName = "type")]
        public MessageType Type { get; set; }
        [JsonProperty(PropertyName = "userToAsk")]
        public int UserToAsk { get; set; }
        [JsonProperty(PropertyName = "howManyPoisToRecommend")]
        public int HowManyPoisToRecommend { get; set; }
        [JsonProperty(PropertyName = "poisToReturn")]
        public List<Poi> PoisToReturn { get; set; }
    }
}