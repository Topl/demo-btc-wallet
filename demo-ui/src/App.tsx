import './App.css'

import TopBar from './components/TopBar';
import TabController from './components/TabController';


function App() {
  return (
    <div className='w-100 flex flex-col'>
      <div className='flex bg-slate-100 border-b'>
        <div className='select-none basis-1/6 p-3 py-4 text-2xl font-thin justify-end'>Demo App</div>
        <TopBar/>
      </div>
      <div className='basis-full flex bg-white'>
        <TabController/>
      </div>
    </div>
  )
}

export default App
