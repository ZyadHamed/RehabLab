using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace RehabLab
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void btnArmFlextion_Click(object sender, RoutedEventArgs e)
        {
            GuidanceWindow guidanceWindow = new GuidanceWindow(0);
            guidanceWindow.Show();
        }

        private void btnLegFlextion_Click(object sender, RoutedEventArgs e)
        {
            GuidanceWindow guidanceWindow = new GuidanceWindow(1);
            guidanceWindow.Show();
        }

        private void btnHeadRotation_Click(object sender, RoutedEventArgs e)
        {
            GuidanceWindow guidanceWindow = new GuidanceWindow(2);
            guidanceWindow.Show();
        }
    }
}