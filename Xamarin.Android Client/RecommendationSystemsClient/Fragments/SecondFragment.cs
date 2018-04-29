using System.Linq;
using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using RecommendationSystemsClient.Services;

namespace RecommendationSystemsClient.Fragments
{
    public class SecondFragment : Android.Support.V4.App.Fragment
    {
        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var root = inflater.Inflate(Resource.Layout.SecondFragment, container, false);

            var imageView = root.FindViewById<ImageView>(Resource.Id.talking_android);

            imageView.Click += (sender, eventArgs) =>
            {
                var alert = new Android.Support.V7.App.AlertDialog.Builder(Context);
                var input = new EditText(Context);

                alert.SetView(input);
                alert.SetPositiveButton("Change Text", (alertPosSender, alertPosEventArgs) =>
                {
                    var tv = root.FindViewById<TextView>(Resource.Id.textView1);
                    tv.Text = input.Text.ToString().Trim();

                    Toast.MakeText(Activity, "Text changed!",
                        ToastLength.Long).Show();
                });

                alert.SetNegativeButton("Cancel", (alertNegSender, alertNegEventArgs) =>
                {
                    if (!(alertNegSender is AlertDialog dialog)) return;

                    dialog.Cancel();
                });

                alert.Show();
            };

            var getPoisImageView = root.FindViewById<ImageView>(Resource.Id.get_pois);
            getPoisImageView.Click += async (o, e) =>
            {
                //Progressbar run here
                
                //TODO Get ip, port, userToAsk, numberOfPois from UI
                var pois = await new NetworkService("192.168.2.2", 20912).GetPois(1, 5);

                if (pois == null) return;

                pois.ForEach(poi =>
                {
                    System.Diagnostics.Debug.WriteLine($"Poi Recieved: {poi.Id}");
                });
                
                //Progressbar stop here
            };

            return root;
        }
    }
}