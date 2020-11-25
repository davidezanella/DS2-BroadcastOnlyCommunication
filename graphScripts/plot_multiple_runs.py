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
            latencies[scenario] = {'x': [], 'y': []}
        latencies[scenario]['x'].append(row['Perturbation']) 
        latencies[scenario]['y'].append(float(row['Latency']))

    for scenario in latencies:
        # plotting the points  
        plt.plot(latencies[scenario]['x'], latencies[scenario]['y'], label=scenario)
    
    # naming the x axis 
    plt.xlabel('Protocol') 
    # naming the y axis 
    plt.ylabel('Latency (in ticks)') 
    
    # show the legend
    plt.legend()
    
    # giving a title to my graph 
    plt.title('Latency graph') 
    
    # function to show the plot 
    plt.show()      


if __name__ == "__main__":
    main()
