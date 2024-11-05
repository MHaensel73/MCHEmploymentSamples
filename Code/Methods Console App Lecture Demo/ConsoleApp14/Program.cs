using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MethodLectureDemo
{
    class Program
    {
        static void DisplayInfo()
        {
            string greeting;
            if (DateTime.Now < DateTime.Parse("12:00 PM"))
                greeting = "Good Morning!";
            else if (DateTime.Now < DateTime.Parse("6:00 PM"))
                greeting = "Good Afternoon!";
            else
                greeting = "Good Evening!";

            Console.WriteLine(greeting);
            Console.WriteLine(" This program computes training heart rate: 0.6*(220-Age-RestingRate");
        }

        static void ComputeTrainingRate(float restingRate, int age)
        {
            float maximumRate, trainingRate;
            // Compute maximum heart rate:
            maximumRate = 220 - age;
            // Compute training heart rate
            trainingRate = 0.6F * (maximumRate - restingRate) + restingRate;

            // Display Training heart rate:
            Console.WriteLine("Your Training Heart Rate is: " + trainingRate.ToString("N0"));           
        }

        static void Main(string[] args)
        {
            string userInput;
            int age;
            float restingRate;

            DisplayInfo();

            Console.WriteLine("Please enter your age.");
            userInput = Console.ReadLine();
            Int32.TryParse(userInput, out age);

            Console.WriteLine("Please enter your resting heart rate.");
            userInput = Console.ReadLine();
            float.TryParse(userInput, out restingRate);

            ComputeTrainingRate(restingRate, age);

            // Wait for the user to press enter before closing the console
            Console.ReadLine();
        }
    }
}
