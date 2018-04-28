using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;

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

            return root;
        }
    }
}