import './App.css'

import Tab from 'react-bootstrap/Tab';
import TopBar from './components/TopBar';
import SideBar from './components/SideBar';
import ContentPane from './components/ContentPane';


function App() {
  return (
    <div className='w-100 flex flex-col'>
      <div className='flex bg-slate-100 border-b'>
        <div className='select-none basis-1/6 p-3 py-4 text-2xl font-thin justify-end'>Demo App</div>
        <TopBar/>
      </div>
      <div className='basis-full flex bg-white'>
        <Tab.Container defaultActiveKey="transfer">
          <SideBar/>
          <ContentPane/>
        </Tab.Container>
      </div>
    </div>
  )
}

export default App
