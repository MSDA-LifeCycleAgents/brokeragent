Make sure that [Node.js](https://nodejs.org/) and [Node-RED](https://nodered.org/) have been installed.

Open your commandpromt or similar program and type in node-red.
Open your browser and go to 127.0.0.1:1880

![Image of settings](/images/settings.JPG)
Go to Settings(Top right 3 bars) and select manage palette. Make sure node-red-dashboard is installed.
Next go [here](nodered.md) and copy the contents of this file unto your clipboard. And import these contents using the import option in thesettings menu again.

In the flow window select the inject node and copy the contents to a text editor. And replace the ip-addresses that are start with 192. to your own.
Also replace this number: <payload-length>611</payload-length> with the number of the content after </envelope> and add 1 to it.
And place all of this back in the inject node.
![Image of content](/images/content.jpg)

Replace the ip-address on the tcp node connected to the inject node.

And finally create a .json file on your system and let ReadFromFile and WriteToFile point to it.

Press deploy and youre ready to go.
The ui is found on 127.0.0.1:1880/ui