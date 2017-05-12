 
 
 
 
 


// APIModel name = Alexa Skills Model
// Appliances count = 2
// Skills count = 3
    

definition(
    name: "HTTP control/query cloud-connected devices App",
    namespace: "MDSE",
    author: "AP",
    description: "Creates HTTP control/query cloud-connected devices",
    category: "Convenience",
    iconUrl: "https://github.com/chancsc/icon/raw/master/standard-tile%401x.png",
    iconX2Url: "https://github.com/chancsc/icon/raw/master/standard-tile@2x.png",
    iconX3Url: "https://github.com/chancsc/icon/raw/master/standard-tile@3x.png",
    singleInstance: true,
    oauth: false)


preferences {
}

def installed() {
	log.info "Installed HTTP control/query with settings: ${settings}"
	initialize()
}

def updated() {
	log.info "Updated HTTP control/query with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	def uri1 = 'http://domain.test'
	def path1 = '/'
	def childId1 = '001'
	def existing1 = getChildDevice(childId1)
	if (!existing1)
	{
		def childDevice1 = addChildDevice('MDSE', 'device1 handler', childId1, null, [name: childId1, label: 'Kitchen Light', data: [url: uri1, pathStr: path1]])
		childDevice1.getInitialValue()
	}
	def uri2 = 'http://domain.test'
	def path2 = '/'
	def childId2 = '002'
	def existing2 = getChildDevice(childId2)
	if (!existing2)
	{
		def childDevice2 = addChildDevice('MDSE', 'device2 handler', childId2, null, [name: childId2, label: 'Bathroom Thermostat', data: [url: uri2, pathStr: path2]])
		childDevice2.getInitialValue()
	}
}

def uninstalled() {
    removeChildDevices(getChildDevices())
}

private removeChildDevices(delete) {
    delete.each {
        deleteChildDevice(it.deviceNetworkId)
    }
}


// ******************************************** Handler for Kitchen Light *******************************************************

metadata {
	definition (name: "device1 handler", namespace: "MDSE", author: "AP") {
    	attribute "state", "string"
		attribute "value", "number"
		command 'TurnOnKitchenLight'
		}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles(scale: 2) {
		standardTile("MainTile", "device.state", width: 2, height: 2, canChangeIcon: true) {
            state "off", label: 'Light is ${currentValue}', icon: "st.switches.light.off", backgroundColor: "#ffffff"
            state "on", label: 'Light is ${currentValue}', icon: "st.switches.light.on", backgroundColor: "#00a0dc"
        }


		standardTile('TurnOnKitchenLight', 'device.state', width: 2, height: 2, canChangeIcon: true)
	{
		state 'default', label: 'TurnOnKitchenLight', action: 'TurnOnKitchenLight', icon: 'st.sonos.play-icon', backgroundColor: '#ffffff'
	}
        main "MainTile"
        	details (["MainTile" , 'TurnOnKitchenLight'])
		
	}    
}

def getInitialValue(){
	log.info "Executing getInitialValue"
	executeCommand("Query", "GetInitialValue", "GetInitialValueRequest", "")
}

	def TurnOnKitchenLight(def arg)
	{
		executeCommand('Control', 'TurnOn', 'TurnOnRequest', arg)
	}

private void executeCommand(String method, String act, String cmd, def val){
    def httpInfo = [
        uri: device.getDataValue("url"),
        path: device.getDataValue("pathStr")
	]
    
    try{
    	if (method == "Control") {
        	httpInfo.body = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD", value: val ]
            log.debug httpInfo
            httpPost(httpInfo) { resp ->
                sendEvent(name: "state", value : resp.data.payload.state)
                sendEvent(name: "value", value : resp.data.payload.value)
            }
        }
        else {
        	httpInfo.query = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD" ]
            log.debug httpInfo
            httpGet(httpInfo) { resp ->
                sendEvent(name: "state", value : resp.data.payload.state)
                sendEvent(name: "value", value : resp.data.payload.value)
            }
        }
    } catch (e) {
        log.error "Error creating device: ${e}"
    }
}

// ******************************************** Handler for Bathroom Thermostat *******************************************************

metadata {
	definition (name: "device2 handler", namespace: "MDSE", author: "AP") {
    	attribute "state", "string"
		attribute "value", "number"
		command 'SetBathroomTemp'
	command 'GetBathroomTemp'
		}

	// simulator metadata
	simulator {
	}


	// UI tile definitions
	tiles(scale: 2) {
		standardTile("MainTile", "device.value", width: 2, height: 2) {
			state "default", label: '${currentValue}°', icon: "st.alarm.temperature.normal", backgroundColors:[
					[value: -10, color: "#800080"],//purple
					[value: 0, color: "#0000ff"], //blue
					[value: 25, color: "#00ff00"],//green
					[value: 35, color: "#ffff00"],//yellow
					[value: 45, color: "#ffa500"],//orange			
					[value: 60, color: "#ff0000"]//red
			]
		}

		controlTile('SetBathroomTemp', 'device.value', 'slider', width: 4, height: 2, inactiveLabel: false, range:'(-100..100)')
	{
		state 'default', label: 'SetBathroomTemp', action: 'SetBathroomTemp'
	}
	standardTile('GetBathroomTemp', 'device.value', width: 2, height: 2, canChangeIcon: true)
	{
		state 'default', label: 'GetBathroomTemp', action: 'GetBathroomTemp', icon: 'st.sonos.play-icon', backgroundColor: '#ffffff'
	}
        main "MainTile"
        	details (["MainTile" , 'SetBathroomTemp', 'GetBathroomTemp'])
		
	}    
}

def getInitialValue(){
	log.info "Executing getInitialValue"
	executeCommand("Query", "GetInitialValue", "GetInitialValueRequest", "")
}

	def SetBathroomTemp(def arg)
	{
		executeCommand('Control', 'SetTargetTemperature', 'SetTargetTemperatureRequest', arg)
	}
	def GetBathroomTemp()
	{
		executeCommand('Query', 'GetTemperatureReading', 'GetTemperatureReadingRequest', '')
	}

private void executeCommand(String method, String act, String cmd, def val){
    def httpInfo = [
        uri: device.getDataValue("url"),
        path: device.getDataValue("pathStr")
	]
    
    try{
    	if (method == "Control") {
        	httpInfo.body = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD", value: val ]
            log.debug httpInfo
            httpPost(httpInfo) { resp ->
                sendEvent(name: "state", value : resp.data.payload.state)
                sendEvent(name: "value", value : resp.data.payload.value)
            }
        }
        else {
        	httpInfo.query = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD" ]
            log.debug httpInfo
            httpGet(httpInfo) { resp ->
                sendEvent(name: "state", value : resp.data.payload.state)
                sendEvent(name: "value", value : resp.data.payload.value)
            }
        }
    } catch (e) {
        log.error "Error creating device: ${e}"
    }
}
