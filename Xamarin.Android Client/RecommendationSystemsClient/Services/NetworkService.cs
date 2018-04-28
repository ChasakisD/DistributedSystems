using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;
using System.Text;
using Newtonsoft.Json;
using RecommendationSystemsClient.Models;

namespace RecommendationSystemsClient.Services
{
    public class NetworkService
    {
        private readonly string _masterIp;
        private readonly int _masterPort;

        /**
         * Constructor
         */
        public NetworkService(string masterId, int masterPort)
        {
            _masterIp = masterId;
            _masterPort = masterPort;
        }

        public List<Poi> GetPois(int userToAsk, int poisToReturn)
        {
            //Initialize the connection client
            var client = new TcpClient(_masterIp, _masterPort);

            //Constuct a new request message
            var message = new CommunicationMessage
            {
                Type = MessageType.AskRecommendation,
                UserToAsk = userToAsk,
                HowManyPoisToRecommend = poisToReturn
            };

            //Serialize via JSON the object to string and the to byte array
            var serializedData = JsonConvert.SerializeObject(message);
            var data = Encoding.UTF8.GetBytes(serializedData);

            string result;

            //Write the message to the stream
            using (var stream = client.GetStream())
            {
                stream.Write(data, 0, data.Length);

                //When we got the connection write the stream to get our response
                using (var streamReader = new StreamReader(stream))
                {
                    result = streamReader.ReadToEnd();
                }
            }

            //Deserialize the response as a CommunicationMessage
            var messageResult = JsonConvert.DeserializeObject<CommunicationMessage>(result);

            //If we got ReplyRecommendation type, return our list
            return messageResult?.Type != MessageType.ReplyRecommendation 
                ? null 
                : messageResult.PoisToReturn;
        }
    }
}