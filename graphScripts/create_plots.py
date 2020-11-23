import csv
import sys
import argparse
import matplotlib.pyplot as plt 

def parse_arg(argv):
    parser = argparse.ArgumentParser()
    parser.add_argument('--stations', type=str, help='Path to stations log file')
    parser.add_argument('--relays', type=str, help='Path to relays log file')
    return parser.parse_args(argv)


args = parse_arg(sys.argv[1:])

file_station = open(str(args.stations))
csv_station = csv.DictReader(file_station, delimiter=',')

file_relay = open(str(args.relays))
csv_relay = csv.DictReader(file_relay, delimiter=',')

stations = {}
relays = {}

#create a dict stations[station_id: [<tick, sent_ref, value>]]
for row in csv_station:
    station_id = row['StationId']
    if station_id not in stations.keys():
        stations[station_id] = []
    if row['NewPerturbationValue'] != "":
        stations[station_id].append({
            'station': row['StationId'],
            'tick': float(row['tick']),
            'sent_ref': int(row['CurrentRef'])-1,
            'value': row['NewPerturbationValue']
        })


#create a dict relays[relay_id: [<tick, station, ref>]]
for row in csv_relay:
    relay_id = row['RelayId']
    if relay_id not in relays.keys():
        relays[relay_id] = []
    if row['ArrivedPerturbations'] != "":
        perturbations = row['ArrivedPerturbations'].split(',')
        for pert in perturbations:
            splitted = list(filter(None, pert.split(' ')))
            relays[relay_id].append({
                'tick': float(row['tick']),
                'station': splitted[0],
                'ref': int(splitted[1])
            })

perturbations = {}

#plot mean latency per perturbation

for station in stations:
    for pert in stations[station]:
        perturbations[pert["station"], pert["sent_ref"]]=[pert["tick"], []]

print("Perturbations received by relays")
#make a list of all sent perturbations
for relay in relays:
    #arr_p means arrived_perturbation, it is not pirate language
    for arr_p in relays[relay]:
        perturbations[arr_p["station"], arr_p["ref"]][1].append({
            'relay': relay,
            'arr_tick': arr_p["tick"]
        })
        print(perturbations[arr_p["station"], arr_p["ref"]])

#compute the mean latency for each pert and draw graph
x = [] #x axis
y = [] #y axis

i = 0
for pert in perturbations:
    x.append(pert)
    count = 0
    latency = 0
    for data in perturbations[pert][1]:
        count+=1
        latency+=data['arr_tick']
    
    latency = latency / count
    perturbations[pert][1] = latency
    y.append(perturbations[pert][1] - perturbations[pert][0])
    i+=1

# plotting the points  
plt.plot(x, y) 
  
# naming the x axis 
plt.xlabel('Station - Ref') 
# naming the y axis 
plt.ylabel('Latency (in ticks)') 
  
# giving a title to my graph 
plt.title('Latency graph') 
  
# function to show the plot 
plt.show() 








