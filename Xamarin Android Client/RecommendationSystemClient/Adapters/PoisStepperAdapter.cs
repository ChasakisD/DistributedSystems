using Android.Content;
using Android.Support.Design.Widget;
using Android.Text;
using Android.Views;
using Android.Widget;
using Java.Lang;
using Moe.Feng.Common.Stepperview;

namespace RecommendationSystemClient.Adapters
{
    public class PoisStepperAdapter : Object, IStepperAdapter
    {
        public VerticalStepperView VerticalStepperView { get; set; }

        public ICharSequence GetSummaryFormatted(int p0)
        {
            switch (p0)
            {
                case 0:
                    return Html.FromHtml("Summarized if needed"
                        + (VerticalStepperView.CurrentStep > p0 ? "; <b>isDone!</b>" : ""));
                default:
                    return Html.FromHtml("Last Step"
                        + (VerticalStepperView.CurrentStep > p0 ? "; <b>isDone!</b>" : ""));
            }
        }

        public ICharSequence GetTitleFormatted(int p0)
        {
            switch (p0)
            {
                case 0: return new String("First");
                default: return new String("First");
            }
        }

        public View OnCreateCustomView(int index, Context context, VerticalStepperItemView parent)
        {
            var inflateView = LayoutInflater.From(context)
                .Inflate(Resource.Layout.PoisStepperViewItem, parent, false);

            var textView = inflateView.FindViewById<TextView>(Resource.Id.item_content);
            textView.Text = index == 0
                ? context.Resources.GetString(Resource.String.content_step_0)
                : context.Resources.GetString(Resource.String.content_step_1);

            var nextButton = inflateView.FindViewById<Button>(Resource.Id.button_next);
            nextButton.Text = index == Size() - 1
                ? "Set error text"
                : context.Resources.GetString(Android.Resource.String.Ok);
            nextButton.Click += (o, e) =>
            {
                if (VerticalStepperView.NextStep()) return;
                VerticalStepperView.SetErrorText(index, VerticalStepperView.GetErrorText(index) == null ? "Test Error" : null);
                Snackbar.Make(VerticalStepperView, "Set!", Snackbar.LengthLong).Show();
            };

            var prevButton = inflateView.FindViewById<Button>(Resource.Id.button_prev);
            prevButton.Text = index == 0
                ? context.Resources.GetString(Resource.String.toggle_animation_button)
                : context.Resources.GetString(Android.Resource.String.Cancel);
            prevButton.Click += (o, e) =>
            {
                if (index == 0) VerticalStepperView.AnimationEnabled = !VerticalStepperView.AnimationEnabled;
                else VerticalStepperView.PrevStep();
            };

            return inflateView;
        }

        public void OnHide(int p0) { }

        public void OnShow(int p0) { }

        public int Size()
        {
            return 2;
        }
    }
}