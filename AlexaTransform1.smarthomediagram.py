import requests
    
def lambda_handler(event, context):
	access_token = event['payload']['accessToken']
	if event['header']['namespace'] == 'Alexa.ConnectedHome.Discovery':
		return handleDiscovery(context, event)
	if event['payload']['appliance']['applianceId'] == '001':
		return handleDevice1(context, event)
	if event['payload']['appliance']['applianceId'] == '002':
		return handleDevice2(context, event)

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
					'applianceId':'001',
					'manufacturerName':'Philips',
					'modelName':'Hue',
					'version':'2.2',
					'friendlyName':'',
					'friendlyDescription':'',
					'isReachable':True,
					'actions':[
						'TurnOn',
					]
				},
				{
					'applianceId':'002',
					'manufacturerName':'EcoBee',
					'modelName':'Thermostat',
					'version':'3.0',
					'friendlyName':'',
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

def handleDevice1(context, event):
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

	if event['header']['name'] == 'TurnOnRequest':
		params = {'id':'001', 'name':'TurnOnRequest', 'action':'TurnOn', 'messageid': message_id, 'deltaPercentage' : deltaPercentage, 'deltaTemperature' : deltaTemperature, 'lockState' : lockState, 'percentageState' : percentageState, 'targetTemperature' : targetTemperature }
		r = requests.post('http://domain.test', data = params)
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


def handleDevice2(context, event):
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

	if event['header']['name'] == 'SetTargetTemperatureRequest':
		params = {'id':'002', 'name':'SetTargetTemperatureRequest', 'action':'SetTargetTemperature', 'messageid': message_id, 'deltaPercentage' : deltaPercentage, 'deltaTemperature' : deltaTemperature, 'lockState' : lockState, 'percentageState' : percentageState, 'targetTemperature' : targetTemperature }
		r = requests.post('http://domain.test', data = params)
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


	if event['header']['name'] == 'GetTemperatureReadingRequest':
		params = {'id':'002', 'name':'GetTemperatureReadingRequest', 'action':'GetTemperatureReading', 'messageid': message_id}
		r = requests.get('http://domain.test', data = params)
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
