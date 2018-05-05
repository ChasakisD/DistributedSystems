using System;
using System.Threading.Tasks;
using Android.App;
using Android.Views;
using Android.Widget;
using CheeseBind;
using Java.Lang;
using RecommendationSystemClient.Services;

#pragma warning disable 649

namespace RecommendationSystemClient.Fragments
{
    public class SearchUserFragment : BaseFragment
    {
        [BindView(Resource.Id.masterIp)]
        private TextView _masterIpTextView;
        
        [BindView(Resource.Id.userId)]
        private TextView _userIdTextView;

        [BindView(Resource.Id.numberOfPois)]
        private TextView _numberOfPoisTextView;
        
        [BindView(Resource.Id.progressBarLayout)]
        private LinearLayout _progressBarLayout;

        protected override int LayoutResource => Resource.Layout.SearchUserFragment;
        protected override string Title => "Recommendations";

        [OnClick(Resource.Id.searchUserButton)]
        public async void SearchUserClick(object sender, EventArgs args)
        {
            _progressBarLayout.Visibility = ViewStates.Visible;

            /* Get the IP and the Port */
            var tokens = _masterIpTextView.Text.Split(':');
            if (tokens.Length != 2) return;

            if (!ValidationService.ValidateIpAddress(tokens[0])) return;
            if (!ValidationService.ValidateInt(tokens[1])) return;

            var pois = await Task.Run(async () => await new NetworkService(tokens[0], int.Parse(tokens[1]))
                .GetPois(int.Parse(_userIdTextView.Text), int.Parse(_numberOfPoisTextView.Text)));
            
            string resultPois;
            if (pois == null)
            {
                resultPois = "Unable to connect! Please check your connectivity!";
            }
            else
            {
                var builder = new StringBuilder();
                pois.ForEach(poi => builder.Append($"Poi Recieved: {poi.Id}\n"));
                resultPois = builder.ToString();
            }
            
            var alert = new Android.Support.V7.App.AlertDialog.Builder(Context);
            var input = new TextView(Context) { Text = resultPois };
            alert.SetView(input);

            alert.SetNegativeButton("Ok", (alertNegSender, alertNegEventArgs) =>
            {
                if (!(alertNegSender is AlertDialog dialog)) return;

                dialog.Cancel();
            });

            alert.Show();

            _progressBarLayout.Visibility = ViewStates.Gone;
        }
    }
}