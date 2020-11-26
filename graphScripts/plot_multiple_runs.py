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
    parser.add_argument('--remove_last', type=int, help='Number of last perturbations to avoid to plot')
    return parser.parse_args(argv)


def main():
    args = parse_arg(sys.argv[1:])
    
    remove_last = int(args.remove_last)
    
    file_runs = open(str(args.input_csv))
    csv_runs = csv.DictReader(file_runs, delimiter=',')

    latencies = {}

    # sort the values
    csv_runs = list(sorted(csv_runs, key=lambda x: (float(x['Tick']), x['Perturbation'])))

    x_labels = {}

    for row in csv_runs:
        scenario = row['Scenario']
        if scenario not in latencies.keys():
            latencies[scenario] = {'x': [], 'y': []}
        latencies[scenario]['x'].append(row['Perturbation']) 
        latencies[scenario]['y'].append(float(row['Latency']))
        x_labels[row['Perturbation']] = row['Tick']

    for scenario in latencies:
        # plotting the points  
        plt.plot(latencies[scenario]['x'][:-remove_last], latencies[scenario]['y'][:-remove_last], label=scenario)
    
    # naming the x axis 
    plt.xlabel('Protocol') 
    plt.xticks(range(len(x_labels.values())-remove_last), x_labels.values()[:-remove_last])
    
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
