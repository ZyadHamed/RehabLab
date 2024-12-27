using Firebase.Database;
using Firebase.Database.Query;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection.Metadata;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace RehabLab
{
    /// <summary>
    /// Interaction logic for MonitorAngleWindow.xaml
    /// </summary>
    public partial class MonitorAngleWindow : Window
    {
        FirebaseClient firebase;
        private const double MIN_ACCEPTABLE_ANGLE_ARM_X = 5;
        private const double MAX_ACCEPTABLE_ANGLE_ARM_X = 163;
        private const double MIN_ACCEPTABLE_ANGLE_ARM_Y = -2;
        private const double MAX_ACCEPTABLE_ANGLE_ARM_Y = 2;
        private const double MIN_ACCEPTABLE_ANGLE_ARM_Z =-2;
        private const double MAX_ACCEPTABLE_ANGLE_ARM_Z = 2;

        private const double MIN_ACCEPTABLE_ANGLE_LEG_X = -2;
        private const double MAX_ACCEPTABLE_ANGLE_LEG_X = 2;
        private const double MIN_ACCEPTABLE_ANGLE_LEG_Y = 0;
        private const double MAX_ACCEPTABLE_ANGLE_LEG_Y = 4;
        private const double MIN_ACCEPTABLE_ANGLE_LEG_Z = -10;
        private const double MAX_ACCEPTABLE_ANGLE_LEG_Z = 140;

        private const double MIN_ACCEPTABLE_ANGLE_HEAD_X = -2;
        private const double MAX_ACCEPTABLE_ANGLE_HEAD_X = 2;
        private const double MIN_ACCEPTABLE_ANGLE_HEAD_Y = -80;
        private const double MAX_ACCEPTABLE_ANGLE_HEAD_Y = 80;
        private const double MIN_ACCEPTABLE_ANGLE_HEAD_Z = -90;
        private const double MAX_ACCEPTABLE_ANGLE_HEAD_Z = 45;
        string lastKey = "";
        int selectedCourse;
        public MonitorAngleWindow(int _selectedCourse)
        {
            InitializeComponent();
            FirebaseApp.Create(new AppOptions()
            {
                Credential = GoogleCredential.FromFile("D:\\Cairo University\\Communication Skills\\RehabLab\\RehabLab\\DataAccess\\serviceaccountkey.json")
            });
            firebase = new FirebaseClient("https://rehablabdatabase-58a90-default-rtdb.europe-west1.firebasedatabase.app/");
            selectedCourse = _selectedCourse;
            if(selectedCourse == 0)
            {
                lbAcceptableRange.Text = "Acceptable Range: " + MIN_ACCEPTABLE_ANGLE_ARM_X.ToString() + "° - " + MAX_ACCEPTABLE_ANGLE_ARM_X.ToString() + "°";
            }
            else if(selectedCourse == 1)
            {
                lbAcceptableRange.Text = "Acceptable Range: " + MIN_ACCEPTABLE_ANGLE_LEG_X.ToString() + "° - " + MAX_ACCEPTABLE_ANGLE_LEG_X.ToString() + "°";
            }
            else if(selectedCourse == 2)
            {
                lbAcceptableRange.Text = "Acceptable Range: " + MIN_ACCEPTABLE_ANGLE_HEAD_X.ToString() + "° - " + MAX_ACCEPTABLE_ANGLE_HEAD_X.ToString() + "°";
            }
            
        }

        private void UpdateAngleDisplay(double x, double y, double z)
        {
            // Update angle display
            AngleDisplay.Text = $"{x:F1}°";

            // Update status based on angle

            bool isAngleGood = false;
            if(selectedCourse == 0)
            {
                isAngleGood = x >= MIN_ACCEPTABLE_ANGLE_ARM_X && x <= MAX_ACCEPTABLE_ANGLE_ARM_X;
            }
            else if(selectedCourse == 1)
            {
                isAngleGood = x >= MIN_ACCEPTABLE_ANGLE_LEG_X && x <= MAX_ACCEPTABLE_ANGLE_LEG_X;
            }
            else if(selectedCourse == 2)
            {
                isAngleGood = x >= MIN_ACCEPTABLE_ANGLE_HEAD_X && x <= MAX_ACCEPTABLE_ANGLE_HEAD_X;
            }
            if (isAngleGood)
            {
                StatusBorder.Background = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#4CAF50")); // Green
                StatusText.Text = "GOOD";
            }
            else
            {
                StatusBorder.Background = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#F44336")); // Red
                StatusText.Text = "BAD";
            }

            // Animate the change
            var fadeAnimation = new DoubleAnimation
            {
                From = 0.8,
                To = 1.0,
                Duration = TimeSpan.FromMilliseconds(200)
            };
            StatusBorder.BeginAnimation(OpacityProperty, fadeAnimation);
        }

        private async void Grid_Loaded(object sender, RoutedEventArgs e)
        {
            DateTime currentTime = DateTime.Now;
            int currentYear = currentTime.Year;
            int currentMonth = currentTime.Month;
            int currentDay = currentTime.Day;
            string currentKey = currentDay.ToString() + "-" + currentMonth.ToString() + "-" + currentYear.ToString();

            // First, get the last key
            var lastEntry = (await firebase
                .Child(currentKey)
                .OrderByKey()
                .LimitToLast(1)
                .OnceAsync<string>()).ToList();

            if (lastEntry.Count > 0)
            {
                lastKey = lastEntry[0].Object;
            }
            firebase.Child(currentKey)
                .OrderByKey()
                .LimitToLast(1)
                .AsObservable<string>()
                .Subscribe(firebaseEvent =>
                {
                    // This will trigger for each new record
                    if (firebaseEvent.EventType == Firebase.Database.Streaming.FirebaseEventType.InsertOrUpdate)
                    {
                        Dispatcher.Invoke(() =>
                        {
                            string[] data = firebaseEvent.Object.Split(';');
                            string gyroAngleX = data[0];
                            string gyroAngleY = data[1];
                            string gyroAngleZ = data[2];
                            UpdateAngleDisplay(double.Parse(gyroAngleX), double.Parse(gyroAngleY), double.Parse(gyroAngleZ));
                            // You can also access the timestamp if needed
                            // var timestamp = firebaseEvent.Object.Timestamp;
                        });
                    }
                });
        }
    }
}
