using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using CheeseBind;
using Com.Diegodobelo.Expandingview;

#pragma warning disable 649

namespace RecommendationSystemClient.Fragments
{
    public class DummyFragment : BaseFragment
    {
        public int No { get; set; }

        [BindView(Resource.Id.expanding_list_main)]
        private ExpandingList _expandingList;

        protected override int LayoutResource => Resource.Layout.DummyFragment;
        protected override string Title => "Dummy";

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var rootView = base.OnCreateView(inflater, container, savedInstanceState);

            CreateItems();

            return rootView;
        }

        private void CreateItems()
        {
            AddItems("John", new [] { "House", "Boat", "Candy", "Collection", "Sport", "Ball", "Head" }, Resource.Color.pink);
            AddItems("Mary", new [] { "Dog", "Horse", "Boat" }, Resource.Color.blue);
            AddItems("Ana", new [] { "Cat" }, Resource.Color.purple);
            AddItems("Peter", new [] { "Parrot", "Elephant", "Coffee" }, Resource.Color.yellow);
            AddItems("Joseph", new [] { "XD" }, Resource.Color.orange);
            AddItems("Paul", new [] { "Golf", "Football" }, Resource.Color.green);
            AddItems("Larry", new [] { "Ferrari", "Mazda", "Honda", "Toyota", "Fiat" }, Resource.Color.blue);
            AddItems("Moe", new [] { "Beans", "Rice", "Meat" }, Resource.Color.yellow);
            AddItems("Bart", new [] { "Hamburger", "Ice cream", "Candy" }, Resource.Color.purple);
        }


        private void AddItems(string title, string[] subItems, int colorRes)
        {
            var item = _expandingList.CreateNewItem(Resource.Layout.ExpandingItemLayout);

            if (item != null)
            {
                item.SetIndicatorColorRes(colorRes);
                item.SetIndicatorIconRes(Resource.Drawable.ic_ghost);

                item.FindViewById<TextView>(Resource.Id.title).Text = title;

                item.CreateSubItems(subItems.Length);
                for (var i = 0; i < subItems.Length; i++)
                {
                    var view = item.GetSubItemView(i);

                    view.FindViewById<TextView>(Resource.Id.sub_title).Text = subItems[i];
                    view.FindViewById(Resource.Id.remove_sub_item).Click += (o, e) => { item.RemoveSubItem(view); };
                }

                item.FindViewById(Resource.Id.add_more_sub_items).Click += (o, e) =>
                {
                    var text = new EditText(Context);
                    var builder = new AlertDialog.Builder(Context);
                    builder.SetView(text);
                    builder.SetTitle("Title");
                    builder.SetPositiveButton(Android.Resource.String.Ok, (s, ev) =>
                    {
                        var newSubItem = item.CreateSubItem();
                        newSubItem.FindViewById<TextView>(Resource.Id.sub_title).Text = text.Text;
                        newSubItem.FindViewById(Resource.Id.remove_sub_item).Click += (s1, s2) =>
                        {
                            item.RemoveSubItem(newSubItem);
                        };
                    });
                    builder.SetNegativeButton(Android.Resource.String.Cancel, (ob, ev1) =>
                    {

                    });
                    builder.Show();
                };

                item.FindViewById(Resource.Id.remove_item).Click += (o, e) => { _expandingList.RemoveItem(item); };
            }
        }
    }
}