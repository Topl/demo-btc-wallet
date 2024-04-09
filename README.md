# Bitcoin Demo Wallet

This repository contains a simple Bitcoin wallet that can be used to send Bitcoin, for the purposes of Topl's BTC Bridge.

## Development

### Front end only development

To start the front end app in development mode, run:
```
cd demo-ui
npm i
npm run dev
```

You can then access the app at `localhost:5173`.

### Back end focused development

First build the front end app:

```
cd demo-ui
npm ci
npm run build
```

Then start the server in development mode:

```
cd ../demo-server
sbt dev
```

This copies the front end app into the server's resources directory, and starts the server on `localhost:3002`.

You can access the app (now connected to a backend) at `localhost:3002`.