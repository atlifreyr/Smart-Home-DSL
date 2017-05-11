 
 
 
 
 


// APIModel name = Alexa Skills Model
// Appliances count = 3
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
	def uri1 = 'http://213.220.94.21'
	def path1 = '/api/Devices'
	def childId1 = 'device001'
	def existing1 = getChildDevice(childId1)
	if (!existing1)
	{
		def childDevice1 = addChildDevice('MDSE', 'bulb-handler-device001', childId1, null, [name: childId1, label: 'Bulb 1', data: [url: uri1, pathStr: path1]])
		childDevice1.getInitialValue()
	}
	def uri2 = 'http://213.220.94.21'
	def path2 = '/api/Devices'
	def childId2 = 'device002'
	def existing2 = getChildDevice(childId2)
	if (!existing2)
	{
		def childDevice2 = addChildDevice('MDSE', 'thermo-handler-device002', childId2, null, [name: childId2, label: 'Thermo 1', data: [url: uri2, pathStr: path2]])
		childDevice2.getInitialValue()
	}
	def uri3 = 'http://213.220.94.21'
	def path3 = '/api/Devices'
	def childId3 = 'device003'
	def existing3 = getChildDevice(childId3)
	if (!existing3)
	{
		def childDevice3 = addChildDevice('MDSE', 'thermo-handler-device003', childId3, null, [name: childId3, label: 'Thermo 2', data: [url: uri3, pathStr: path3]])
		childDevice3.getInitialValue()
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


// ************************************************************************


metadata {
	definition (name: "bulb-handler-device001", namespace: "MDSE", author: "AP") {
    	attribute "state", "string"
		attribute "value", "number"
		command 'TurnOnSkill'
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles(scale: 2) {
		standardTile("Light Status", "device.state", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Light is ${currentValue}', icon: "st.switches.light.off", backgroundColor: "#ffffff"
			state "on", label: 'Light is ${currentValue}', icon: "st.switches.light.on", backgroundColor: "#00a0dc"
		}

		standardTile('TurnOnSkill', 'device.state', width: 2, height: 2, canChangeIcon: true)
		{
			state 'default', label: 'TurnOnSkill', action: 'TurnOnSkill', icon: 'st.sonos.play-icon', backgroundColor: '#ffffff'
		}
		main "Light Status"
			details (["Light Status" , 'TurnOnSkill'])		
	}    
}

def getInitialValue(){
	log.info "Executing getInitialValue"
	executeCommand("Query", "GetInitialValue", "GetInitialValueRequest", "")
}

def TurnOnSkill(def arg)
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
// ************************************************************************



// ************************************************************************



metadata {
	definition (name: "thermo-handler-device002", namespace: "MDSE", author: "AP") {
    	attribute "state", "string"
		attribute "value", "number"
		command 'gettemperaturereadingSkill'
		}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles(scale: 2) {
		standardTile("Temperature", "device.value", width: 2, height: 2) {
			state "default", label: '${currentValue}°', icon: "st.alarm.temperature.normal", backgroundColors:[
					[value: -10, color: "#800080"],//purple
					[value: 0, color: "#0000ff"], //blue
					[value: 25, color: "#00ff00"],//green
					[value: 35, color: "#ffff00"],//yellow
					[value: 45, color: "#ffa500"],//orange			
					[value: 60, color: "#ff0000"]//red
			]
		}

		standardTile('gettemperaturereadingSkill', 'device.value', width: 2, height: 2, canChangeIcon: true)
		{
			state 'default', label: 'gettemperaturereadingSkill', action: 'gettemperaturereadingSkill', icon: 'st.sonos.play-icon', backgroundColor: '#ffffff'
		}
		main "Temperature"
        	details (["Temperature" , 'gettemperaturereadingSkill'])
		
	}
}

def getInitialValue(){
	log.info "Executing getInitialValue"
	executeCommand("Query", "GetInitialValue", "GetInitialValueRequest", "")
}

def gettemperaturereadingSkill()
{
	executeCommand('Query', 'gettemperaturereading', 'gettemperaturereadingRequest', '')
}

private void executeCommand(String method, String act, String cmd, def val){
	def httpInfo = [
		uri: device.getDataValue("url"),
		path: device.getDataValue("pathStr")
	]
    
	try{
    	if (method == "Control") {
        	httpInfo.body = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD", value: val]
			log.debug httpInfo
			httpPost(httpInfo) { resp ->
				sendEvent(name: "state", value : resp.data.payload.state)
				sendEvent(name: "value", value : resp.data.payload.value)
			}
		}
		else {
        	httpInfo.query = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD"]
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



// ************************************************************************


metadata {
	definition (name: "thermo-handler-device003", namespace: "MDSE", author: "AP") {
    	attribute "state", "string"
		attribute "value", "number"
		command 'settargettemperatureSkill'
		}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles(scale: 2) {
		standardTile("Temperature", "device.value", width: 2, height: 2) {
			state "default", label: '${currentValue}°', icon: "st.alarm.temperature.normal", backgroundColors:[
					[value: -10, color: "#800080"],//purple
					[value: 0, color: "#0000ff"], //blue
					[value: 25, color: "#00ff00"],//green
					[value: 35, color: "#ffff00"],//yellow
					[value: 45, color: "#ffa500"],//orange			
					[value: 60, color: "#ff0000"]//red
			]
		}

		controlTile('settargettemperatureSkill', 'device.value', 'slider', width: 4, height: 2, inactiveLabel: false, range:'(-100..100)')
		{
			state 'default', label: 'settargettemperatureSkill', action: 'settargettemperatureSkill'
		}
		main "Temperature"
        	details (["Temperature" , 'settargettemperatureSkill'])
		
	}
}

def getInitialValue(){
	log.info "Executing getInitialValue"
	executeCommand("Query", "GetInitialValue", "GetInitialValueRequest", "")
}

def settargettemperatureSkill(def arg)
{
	executeCommand('Control', 'settargettemperature', 'settargettemperatureRequest', arg)
}

private void executeCommand(String method, String act, String cmd, def val){
	def httpInfo = [
		uri: device.getDataValue("url"),
		path: device.getDataValue("pathStr")
	]
    
	try{
    	if (method == "Control") {
        	httpInfo.body = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD", value: val]
			log.debug httpInfo
			httpPost(httpInfo) { resp ->
				sendEvent(name: "state", value : resp.data.payload.state)
				sendEvent(name: "value", value : resp.data.payload.value)
			}
		}
		else {
        	httpInfo.query = [ id : device.getName(), CallerIdentifier: "smartthing", name:cmd, action:act, messageid: "8ABF9885-B386-443D-8ABA-01EA780199FD"]
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



// ************************************************************************

