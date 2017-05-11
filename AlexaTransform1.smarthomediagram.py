 
 
 
 
 
import requests

# APIModel name = Alexa Skills Model
# Appliances count = 2
# Skills count = 3
    
def lambda_handler(event, context):
	access_token = event['payload']['accessToken']
	if event['header']['namespace'] == 'Alexa.ConnectedHome.Discovery':
		return handleDiscovery(context, event)
	if event['header']['namespace'] == 'Alexa.ConnectedHome.Control':
		return handleControl(context, event)
	if event['header']['namespace'] == 'Alexa.ConnectedHome.Query':
		return handleQuery(context, event)

def handleDiscovery(context, event):
	payload = ''
	header = ''
	if event['header']['name'] == 'DiscoverAppliancesRequest':
		header = {
			'namespace':'Alexa.ConnectedHome.Discovery',
			'name':'DiscoverAppliancesResponse',
			'payloadVersion':'2'
		}
		payload = {
			'discoveredAppliances': [
				{
					'applianceId':'device001',
					'manufacturerName':'Philips',
					'modelName':'Hue',
					'version':'4.20',
					'friendlyName':'Hue Bulb',
					'friendlyDescription':'',
					'isReachable':True,
					'actions':[
						'TurnOn',
						'TurnOff',
					]
				},
				{
					'applianceId':'device002',
					'manufacturerName':'EcoBee',
					'modelName':'ModelName',
					'version':'3.3',
					'friendlyName':'Thermostat 1',
					'friendlyDescription':'',
					'isReachable':True,
					'actions':[
						'GetTargetTemperature',
					]
				},
			]
		}
	return { 'header': header, 'payload': payload }

def handleControl(context, event):
	payload = {}
	name = ''
	responseMessageId = ''
	device_id = event['payload']['appliance']['applianceId']
	message_id = event['header']['messageId']

	deltaPercentage = ''
	deltaTemperature = ''
	lockState = ''
	percentageState = ''
	targetTemperature = ''
	# DecrementPercentageRequest and IncrementPercentageRequest
	if 'deltaPercentage' in event['payload']:
		deltaPercentage = event['payload']['deltaPercentage']['value']
	# IncrementTargetTemperatureRequest
	if 'deltaTemperature' in event['payload']:
		deltaTemperature = event['payload']['deltaTemperature']['value']
	# SetLockStateRequest
	if 'lockState' in event['payload']:
		lockState = event['payload']['lockState']
	# SetPercentageRequest
	if 'percentageState' in event['payload']:
		percentageState = event['payload']['percentageState']['value']
	# SetTargetTemperatureRequest
	if 'targetTemperature' in event['payload']:
		targetTemperature = event['payload']['targetTemperature']['value']

	if event['header']['name'] == 'TurnOnRequest' and device_id == 'device001':
		params = {'id':'device001', 'name':'TurnOnRequest', 'action':'TurnOn', 'messageid': message_id, 'deltaPercentage' : deltaPercentage, 'deltaTemperature' : deltaTemperature, 'lockState' : lockState, 'percentageState' : percentageState, 'targetTemperature' : targetTemperature }
		r = requests.post('http://213.220.94.21/api/Devices', data = params)
		response = r.json() 
		if r.status_code == 200:
			name = 'TurnOnConfirmation'
			responseMessageId = response['header']['messageId']
			payload = response['payload']
		else:
			name = 'TurnOnFailed'
		header = {
                    "namespace":"Alexa.ConnectedHome.Control",
                    "name": name,
                    "payloadVersion":"2",
                    "messageId": responseMessageId
                    }
		return { 'header': header, 'payload': payload }

	if event['header']['name'] == 'TurnOffRequest' and device_id == 'device001':
		params = {'id':'device001', 'name':'TurnOffRequest', 'action':'TurnOff', 'messageid': message_id, 'deltaPercentage' : deltaPercentage, 'deltaTemperature' : deltaTemperature, 'lockState' : lockState, 'percentageState' : percentageState, 'targetTemperature' : targetTemperature }
		r = requests.post('http://213.220.94.21/api/Devices', data = params)
		response = r.json() 
		if r.status_code == 200:
			name = 'TurnOffConfirmation'
			responseMessageId = response['header']['messageId']
			payload = response['payload']
		else:
			name = 'TurnOffFailed'
		header = {
                    "namespace":"Alexa.ConnectedHome.Control",
                    "name": name,
                    "payloadVersion":"2",
                    "messageId": responseMessageId
                    }
		return { 'header': header, 'payload': payload }

	# If none of the if statements above have run return error
	header = {
		'namespace':'Alexa.ConnectedHome.Control',
		'name':'Unknown command or command not supported by targeted device',
		'payloadVersion':'2'
	}
	payload = {}
	return { 'header': header, 'payload': payload }

def handleQuery(context, event):
	payload = {}
	name = ''
	responseMessageId = ''
	device_id = event['payload']['appliance']['applianceId']
	message_id = event['header']['messageId']

	if event['header']['name'] == 'GetTargetTemperatureRequest' and device_id == 'device002':
		params = {'id':'device002', 'name':'GetTargetTemperatureRequest', 'action':'GetTargetTemperature', 'messageid': message_id}
		r = requests.get('http://213.220.94.21/api/Devices', data = params)
		response = r.json() 
		if r.status_code == 200:
			name = 'GetTargetTemperatureResponse'
			responseMessageId = response['header']['messageId']
			payload = response['payload']
		else:
			name = 'GetTargetTemperatureFailed'
		header = {
                    "namespace":"Alexa.ConnectedHome.Query",
                    "name": name,
                    "payloadVersion":"2",
                    "messageId": responseMessageId
                    }
		return { 'header': header, 'payload': payload }
