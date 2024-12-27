using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace RehabLab
{
    /// <summary>
    /// Interaction logic for GuidanceWindow.xaml
    /// </summary>
    public partial class GuidanceWindow : Window
    {
        int GuidanceIndex;
        public GuidanceWindow(int _GuidanceIndex)
        {
            InitializeComponent();
            GuidanceIndex = _GuidanceIndex;
            if(GuidanceIndex == 0)
            {
                    var bitmapImage = new BitmapImage(new Uri("/images/handGuidance.jpg", UriKind.Relative));
                imgGuidance.Source = bitmapImage;
            }
            else if(GuidanceIndex == 1)
            {
                var bitmapImage = new BitmapImage(new Uri("/images/legGuidance.jpg", UriKind.Relative));
                imgGuidance.Source = bitmapImage;
                lbInstructions.Text = "Place the sensor immediately below the knee joint";
            }
            else if (GuidanceIndex == 2)
            {
                var bitmapImage = new BitmapImage(new Uri("/images/neck.jpg", UriKind.Relative));
                imgGuidance.Source = bitmapImage;
                lbInstructions.Text = "Place the sensor in the in the right side in the neck, vertically in the middle";
            }
        }

        private void btnDone_Click(object sender, RoutedEventArgs e)
        {
            MonitorAngleWindow monitorAngleWindow = new MonitorAngleWindow(GuidanceIndex);
            monitorAngleWindow.Show();
            
        }

        private void Grid_Loaded(object sender, RoutedEventArgs e)
        {

        }
    }
}
