import './App.css'
import Transfer from './views/Transfer';
import LoadWallet from './views/LoadWallet';
import CreateWallet from './views/CreateWallet';
import Nav from 'react-bootstrap/Nav';
import Tab from 'react-bootstrap/Tab';


function App() {
  return (
    <div className='w-100 flex flex-col'>
      <div className='flex bg-slate-100 border-b'>
        <div className='select-none basis-1/6 p-3 py-4 text-2xl font-thin justify-end'>Demo App</div>
        <Nav activeKey="btc" defaultActiveKey="btc" className='basis-11/12 items-end'>
          <Nav.Link eventKey="bridge" className='select-none p-4 !text-slate-700 [&.active]:font-bold [&.active]:pointer-events-none'>Bridge</Nav.Link>
          <Nav.Link eventKey="btc" className='select-none p-4 !text-slate-700 [&.active]:font-bold [&.active]:pointer-events-none'>Demo BTC Wallet</Nav.Link>
          <Nav.Link eventKey="topl" className='select-none p-4 !text-slate-700 [&.active]:font-bold [&.active]:pointer-events-none'>Demo Topl Wallet</Nav.Link>
        </Nav>
      </div>
      <div className='basis-full flex bg-white'>
        <Tab.Container defaultActiveKey="transfer">
          <Nav className="basis-1/6 flex-column border-r">
            <Nav.Link eventKey="create" className='select-none p-3 hover:bg-slate-200 !text-slate-700 [&.active]:bg-slate-300 [&.active]:font-semibold [&.active]:pointer-events-none'>Create Wallet</Nav.Link>
            <Nav.Link eventKey="load" className='select-none p-3 hover:bg-slate-200 !text-slate-700 [&.active]:bg-slate-300 [&.active]:font-semibold [&.active]:pointer-events-none'>Load Wallet</Nav.Link>
            <Nav.Link eventKey="transfer" className='select-none p-3 hover:bg-slate-100 !text-slate-700 [&.active]:bg-slate-300 [&.active]:font-semibold [&.active]:pointer-events-none'>Transfer BTC</Nav.Link>
          </Nav>
          <Tab.Content className='basis-11/12 '>
            <Tab.Pane eventKey="create"><CreateWallet/></Tab.Pane>
            <Tab.Pane eventKey="load"><LoadWallet/></Tab.Pane>
            <Tab.Pane eventKey="transfer"><Transfer/></Tab.Pane>
          </Tab.Content>
        </Tab.Container>
      </div>
    </div>
  )
}

export default App
