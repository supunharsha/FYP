import thread
##from serial import Serial
from ws4py.client.threadedclient import WebSocketClient
import time
import re
import socket 
import json   
import sys
from time import sleep
from time import gmtime, strftime

CRITICAL_BATTERY_LEVEL              = 3.6
BROADCAST_NETWORK                   = '192.168.8.255'
WEBSOCKET_SERVER_ENDPOINT           = "ws://localhost:8080/Coordinator/websocketserver"

class Priority:
    LOW                             = 0
    NORMAL                          = 1
    HIGH                            = 2


##-------------------- List of Intrrupts ---------------------------##

class Interrupt:
    NO_EVENT                        = 0
    INCOMING_MESSAGE                = 1    
    CRITICAL_BATTERY_LEVEL_REACHED  = 2
    REGISTER_TO_SERVICE             = 3
    
    



##-------------------- List of Intrrupts ---------------------------##
class Message:
    READY_TO_WORK                   = 0
    ASK_FOR_OTHER_AGENTS            = 1    
    LIST_OF_OTHER_AGENTS            = 2
    LOCATION_MAP                    = 3
    ASSIGNED_AREA                   = 4
    PERSONS_DETAILS                 = 5
    SUSPICIOUS_PERSON               = 6
    CURRENT_BATTERY_VOLTAGE         = 7
    CRITICAL_BATTERY_LEVEL          = 8
    PERSON_DETECTED                 = 9
    CURRENT_LOCATION                = 10


##-------------------- Battery Monitoring Service ---------------------------##



def monitorBatteries():

##    serialPort = Serial("/dev/ttyAMA0", 9600, timeout=2)
##    if (serialPort.isOpen() == False):
##        serialPort.open()
##
##    outStr = ''
##    inStr = ''
##
##    serialPort.flushInput()
##    serialPort.flushOutput()

    while True:
##        response = serialPort.readline()
        response = "3.81-----3.61"
        status = False
##        print response

        values = response.split("-----")
        for value in values :
            value = float(re.findall("\d+\.\d+", value)[0])
            if value < float(CRITICAL_BATTERY_LEVEL):
                status = True
            else:
                status = False
        if status == True:             
            thread.interrupt_main()
            print "Critical Battery Level"
            break
#    serialPort.close()

##-------------------- End of Battery Monitoring Service---------------------------##


##-------------------- Register to the Service via Coordinator---------------------##
def registerToService ():    
    addr = (BROADCAST_NETWORK, 33333) # broadcast address explicitly

    UDPSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # Create socket

    print "Starting the registering process"

    data1 = "register"
    # Almost infinite loop... ;)
    while True:    
        if UDPSock.sendto(data1, addr):
                print "Sending message '%s'..." % data1                
                try:
                        UDPSock.settimeout(5)
                        data, addr = UDPSock.recvfrom(1024)
                        UDPSock.close()
                        print "Server IP is %s" %(data)
                        data1 = data
                        UDPSock.close()                        
                        break
                except:
                        print "Time out exception"




    UDPSock.close()             # Close socket
    print 'Client stopped.'
    return data1;
##-------------------- End of Register to the Service ------------------------------##



##-------------------- WebSocket Connection ----------------------------------------##

class DummyClient(WebSocketClient):
    def opened(self): 
        print "Connection Established"
        
    def closed(self, code, reason=None):
        print "Connection Closed down", code, reason
        

    def received_message(self, m):
        print m



##-------------------- Agent Main Process ------------------------------------------##   
def agentMainProcess(currentEvent,group):
    if(currentEvent != Interrupt.NO_EVENT ):
        if(currentEvent == Interrupt.REGISTER_TO_SERVICE):
            ws = group.get("Coordinator",None);
            msg = formatTheMessageandSend(Message.READY_TO_WORK,Message.READY_TO_WORK,"","Coordinator",Priority.NORMAL);            
            ws.send(msg)
            currentEvent = Interrupt.NO_EVENT
        elif(currentEvent == Interrupt.INCOMING_MESSAGE):
            print "income msg"
            currentEvent = Interrupt.NO_EVENT
                 
##-------------------- Message extract ------------------------------------------##   
def extractTheMessage():
    print "prod"

##-------------------- Message Format ------------------------------------------##   
def formatTheMessageandSend(msg, tag, sender, receiver,priority):
    
    message =   {   'Header'  : 
                            {
                             'Sender'              : sender, 
                             'Reciever'            : receiver,
                             'TimeStamp'           : strftime("%Y-%m-%d %H:%M:%S", gmtime()),
                             'Tag'                 : tag,
                             'Priority'            : priority,
                             'CoomunicatorGroup'   : "",
                            },
                    'Body'    :
                            {
                             'Message'              : msg                             
                             }
                }
    
    msgPacket = json.dumps(message)    
    return msgPacket


##-------------------- End of WebSocket Connection ---------------------------------##



def main():
    try:        
        currentEvent = Interrupt.NO_EVENT
        CommunicatorGroup = {}

        ######### Start Battery Monitoring Thread #####
        thread.start_new_thread(monitorBatteries,())

        
        ######### Register to the Service #############
        serverIp = registerToService()
        newEndPoint = "ws://"+serverIp+":"+WEBSOCKET_SERVER_ENDPOINT.split(":")[2]


        ######### Create Web Socket Connection ########
        ws = DummyClient(newEndPoint)
        ws.connect()
        CommunicatorGroup["Coordinator"] = ws
        currentEvent = Interrupt.REGISTER_TO_SERVICE
         
        ######### Start the main process of Agent #######
        while True:          
            try:                
                agentMainProcess(currentEvent,CommunicatorGroup);
                sleep(60*60)
            except :
                print('interrupted')
             
##------synchronization  error recovery methods
        

        ######### Wait for map #########################



     
        
    
    except KeyboardInterrupt:
       print "Exception"
       ws.close()

    print "App closed"

        
if __name__ == "__main__":
    main()
