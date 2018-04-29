using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
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

        public async Task<List<Poi>> GetPois(int userToAsk, int poisToReturn)
        {
            try
            {
                //Initialize the connection client
                using (var client = new TcpClient(_masterIp, _masterPort))
                {
                    //Constuct a new request message
                    var message = new CommunicationMessage
                    {
                        Type = MessageType.AskRecommendation,
                        UserToAsk = userToAsk,
                        HowManyPoisToRecommend = poisToReturn
                    };

                    //Serialize via JSON the object to string and the to byte array
                    var serializedData = JsonConvert.SerializeObject(message);
                    var messageBytes = Encoding.UTF8.GetBytes(serializedData);

                    //Add an empty header of 4 bytes
                    var messageWithEmptyHeader = new byte[messageBytes.Length + 4];
                    for (var i = 0; i < messageBytes.Length; i++)
                    {
                        messageWithEmptyHeader[4 + i] = messageBytes[i];
                    }

                    //The Json result we've got from the Master
                    string result;

                    //Write the message to the stream
                    using (var stream = client.GetStream())
                    {
                        stream.Write(messageWithEmptyHeader, 0, messageWithEmptyHeader.Length);

                        //When we got the connection write the stream to get our response
                        using (var streamReader = new StreamReader(stream))
                        {
                            result = await streamReader.ReadToEndAsync();
                        }
                    }

                    //Deserialize the response as a CommunicationMessage
                    var messageResult = JsonConvert.DeserializeObject<CommunicationMessage>(result.Substring(4));

                    //If we got ReplyRecommendation type, return our list
                    return messageResult?.Type != MessageType.ReplyRecommendation
                        ? null
                        : messageResult.PoisToReturn;
                }
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine(e.Message);
                return null;
            }
        }
    }
}