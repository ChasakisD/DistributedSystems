using Android.OS;
using Android.Views;
using Android.Widget;
using CheeseBind;

#pragma warning disable 649

namespace RecommendationSystemClient.Fragments
{
    public class DummyFragment : BaseFragment
    {
        public int No { get; set; }

        [BindView(Resource.Id.section_label)]
        private TextView _textView;

        protected override int LayoutResource => Resource.Layout.DummyFragment;
        protected override string Title => "Dummy";

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var rootView = base.OnCreateView(inflater, container, savedInstanceState);

            _textView.Text = $"Hello World {No}";

            return rootView;
        }
    }
}