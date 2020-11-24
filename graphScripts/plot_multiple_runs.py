import csv
import os
import sys
import argparse
import matplotlib.pyplot as plt 
import statistics

'''
    Script used to plot the data in the csv file created via create_plots.py (run in mode (2))
    This script will take the data in the csv file and draw a plot showing the different median latencies
    for the different batch runs on each protocol
'''

def parse_arg(argv):
    parser = argparse.ArgumentParser()
    parser.add_argument('--input_csv', type=str, help='Path to csv file generated via create_plots.py')
    return parser.parse_args(argv)


def main():
    args = parse_arg(sys.argv[1:])
    
    file_runs = open(str(args.input_csv))
    csv_runs = csv.DictReader(file_runs, delimiter=',')

    latencies = {}

    for row in csv_runs:
        scenario = row['Scenario']
        if scenario not in latencies.keys():
            latencies[scenario] = []
        latencies[scenario].append(float(row['Latency']))      

    x = latencies.keys()
    y = []

    for scenario in x:
        y.append(statistics.median(latencies[scenario]))

    # plotting the points  
    plt.bar(x, y, color='green', width=0.5)
    
    # naming the x axis 
    plt.xlabel('Protocol') 
    # naming the y axis 
    plt.ylabel('Latency (in ticks)') 
    
    # giving a title to my graph 
    plt.title('Latency graph') 
    
    # function to show the plot 
    plt.show()      



if __name__ == "__main__":
    main()