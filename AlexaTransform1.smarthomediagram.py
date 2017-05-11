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
					'version':'1.0',
					'friendlyName':'Kitchen Lightbulb',
					'friendlyDescription':'',
					'isReachable':True,
					'actions':[
						'TurnOn',
					]
				},
				{
					'applianceId':'device002',
					'manufacturerName':'EcoBee',
					'modelName':'EB-STATe3-O2',
					'version':'1.0',
					'friendlyName':'Bathroom Thermostat',
					'friendlyDescription':'',
					'isReachable':True,
					'actions':[
						'SetTargetTemperature',
						'GetTemperatureReading',
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
		r = requests.post('<someURL>', data = params)
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

	if event['header']['name'] == 'SetTargetTemperatureRequest' and device_id == 'device002':
		params = {'id':'device002', 'name':'SetTargetTemperatureRequest', 'action':'SetTargetTemperature', 'messageid': message_id, 'deltaPercentage' : deltaPercentage, 'deltaTemperature' : deltaTemperature, 'lockState' : lockState, 'percentageState' : percentageState, 'targetTemperature' : targetTemperature }
		r = requests.post('<someURL>', data = params)
		response = r.json() 
		if r.status_code == 200:
			name = 'SetTargetTemperatureConfirmation'
			responseMessageId = response['header']['messageId']
			payload = response['payload']
		else:
			name = 'SetTargetTemperatureFailed'
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

	if event['header']['name'] == 'GetTemperatureReadingRequest' and device_id == 'device002':
		params = {'id':'device002', 'name':'GetTemperatureReadingRequest', 'action':'GetTemperatureReading', 'messageid': message_id}
		r = requests.get('<someURL>', data = params)
		response = r.json() 
		if r.status_code == 200:
			name = 'GetTemperatureReadingResponse'
			responseMessageId = response['header']['messageId']
			payload = response['payload']
		else:
			name = 'GetTemperatureReadingFailed'
		header = {
                    "namespace":"Alexa.ConnectedHome.Query",
                    "name": name,
                    "payloadVersion":"2",
                    "messageId": responseMessageId
                    }
		return { 'header': header, 'payload': payload }
