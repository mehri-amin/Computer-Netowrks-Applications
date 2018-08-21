# Simple Web Server

import socket 
host = ''
port = 8080

# create a socket, TCP protocol, connection-oriented TCP byte stream
serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# setting socket options
# Ensure reusability of socket.
serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

# Binding port to socket
serverSocket.bind((host, port))

# Tell computer to wait and listen on that port one at a time
serverSocket.listen(1)
    
print("Server is up and running\n")

# Handling server requests
while 1:

	# accepts request and creates a file object to interact with
  csock, caddr = serverSocket.accept()
  
  try:
      request = csock.recv(1024) # recieves get request
      message = request.split()[1] # get file
      filename = message.replace('/', '') # remove backslash
      print filename, '\n' 

      file = open(filename)   # open file 
      # store requested file in a temp buffer
      tempbuf = file.read()

      # send HTTP response header to connection socket
      csock.send('HTTP/1.1 200 OK\r\n\r\n')
      csock.send(tempbuf) #send file

      csock.close() # close socket connection

    # IO Exception
  except IOError:
      csock.send('HTTP/1.1 404 File not found\n\n')
      csock.send('<html><h1>404 Error: File not found</h1></html>')
      csock.close()




