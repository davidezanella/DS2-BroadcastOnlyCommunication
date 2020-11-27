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


def plot_latency(latencies, x, x_labels, remove_last, output_basename):
    for scenario in latencies:
        # plotting the points  
        plt.plot(x[:-remove_last], latencies[scenario][:-remove_last], label=scenario, alpha=0.7)
    
    # naming the x axis 
    plt.xlabel('Perturbation') 
    x_labels = list(x_labels.values())[:-remove_last]
    plt.xticks(range(len(x_labels)), x_labels)
    
    # naming the y axis 
    plt.ylabel('Latency (in ticks)') 
    
    # show the legend
    plt.legend()
    
    # set y min value to 0
    axes = plt.gca()
    axes.set_ylim([0, None])
    
    # giving a title to my graph 
    plt.title('Latency graph') 
    
    # set layout config (to avoid overlapping of labels)
    plt.tight_layout()
    
    # function to show the plot 
    #plt.show()
    
    # save the plot
    plt.savefig(output_basename + '_latency.jpg')
    
    # clear the current figure
    plt.clf()


def plot_relays_reached(relays_reached, x, x_labels, remove_last, output_basename):
    for scenario in relays_reached:
        # plotting the points  
        plt.plot(x[:-remove_last], relays_reached[scenario][:-remove_last], label=scenario, alpha=0.7)
    
    # naming the x axis 
    plt.xlabel('Perturbation') 
    x_labels = list(x_labels.values())[:-remove_last]
    plt.xticks(range(len(x_labels)), x_labels)
    
    # naming the y axis 
    plt.ylabel('Reached relays')
    
    # show the legend
    plt.legend()
    
    # set y min value to 0
    axes = plt.gca()
    axes.set_ylim([0, None])
    
    # giving a title to my graph 
    plt.title('Reached relays graph') 
    
    # set layout config (to avoid overlapping of labels)
    plt.tight_layout()
    
    # function to show the plot 
    #plt.show()
    
    # save the plot
    plt.savefig(output_basename + '_relays.jpg')
    
    # clear the current figure
    plt.clf()


def main():
    args = parse_arg(sys.argv[1:])
    
    remove_last = int(args.remove_last)
    
    output_basename = str(args.input_csv)[:-4]
    
    file_runs = open(str(args.input_csv))
    csv_runs = csv.DictReader(file_runs, delimiter=',')

    latencies = {}
    relays_reached = {}

    # sort the values
    csv_runs = list(sorted(csv_runs, key=lambda x: (float(x['Tick']), x['Perturbation'])))

    x_labels = {}
    x = []

    for row in csv_runs:
        scenario = row['Scenario']
        if scenario not in latencies.keys():
            latencies[scenario] = []
            relays_reached[scenario] = []

        latencies[scenario].append(float(row['Latency']))
        relays_reached[scenario].append(float(row['Count']))
        
        x.append(row['Perturbation'])
        x_labels[row['Perturbation']] = str(int(float(row['Tick']))) + "\ns0"

    x = list(set(x))
    
    plot_latency(latencies, x, x_labels, remove_last, output_basename)
    plot_relays_reached(relays_reached, x, x_labels, remove_last, output_basename)


if __name__ == "__main__":
    main()
