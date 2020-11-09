import csv


station_filename = "../output/OutputStation.2020.Nov.09.13_36_10.txt"
relay_filename = "../output/OutputRelay.2020.Nov.09.13_36_10.txt"

file_station = open(station_filename)
csv_station = csv.DictReader(file_station, delimiter=',')

file_relay = open(relay_filename)
csv_relay = csv.DictReader(file_relay, delimiter=',')

stations = {}
for row in csv_station:
    station_id = row['StationId']
    if station_id not in stations.keys():
        stations[station_id] = []
    if row['NewPerturbationValue'] != "":
        stations[station_id].append({
            'tick': float(row['tick']),
            'sent_ref': int(row['CurrentRef'])-1,
            'value': row['NewPerturbationValue']
        })

relays = {}
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
    

tot = 0
count = 0
for station in stations.keys():
    for p_sent in stations[station]:
        for relay in relays.keys():
            available_pert = list(filter(lambda x: 
                x['station'] == station and
                x['ref'] == p_sent['sent_ref'], relays[relay]))
            min_tick = min(available_pert, key=lambda x: x['tick'])
            tot += min_tick['tick'] - p_sent['tick']
            count += 1

print("Tot:", tot)
print("Count:", count)
print("Mean perturbation time:", tot / count)
