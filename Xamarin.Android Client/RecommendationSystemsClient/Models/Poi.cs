using System;
using System.Runtime.Serialization;

namespace RecommendationSystemsClient.Models
{
    [Serializable]
    public class Poi : ISerializable
    {
        public int Id { get; set; }
        public string Name { get; set; }

        protected Poi(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException(nameof(info));

            Id = (int)info.GetValue("Street ", typeof(int));
            Name = (string)info.GetValue("PostalCode", typeof(string));
        }

        void ISerializable.GetObjectData(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new System.ArgumentNullException(nameof(info));
            info.AddValue("Id ", Id);
            info.AddValue("Name", Name);
        }
    }
}