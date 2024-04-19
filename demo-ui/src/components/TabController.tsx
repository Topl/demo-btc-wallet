import Transfer from '../views/Transfer';
import Wallet from '../views/Wallet';
import Tab from 'react-bootstrap/Tab';
import SideBar from './SideBar';
import { useState } from 'react';
import listTransactions, {TransactionResult} from '../services/listTransactions';
import getBalances, {Balances} from '../services/fetchBalances';

function TabController() {
  const walletName = "default"
  const [balances, setBalances] = useState<Balances>({trusted: 0, untrustedPending: 0, immature: 0})
  const [transactions, setTransactions] = useState<Array<TransactionResult>>([])
  const updateWallet = () => {
    getBalances(walletName).then(setBalances)
    listTransactions(walletName).then(setTransactions)
  }
    return <Tab.Container defaultActiveKey="transfer">
    <SideBar updateWallet={updateWallet}/>
    <Tab.Content className='basis-11/12 '>
      <Tab.Pane eventKey="view"><Wallet balances={balances} transactions={transactions}/></Tab.Pane>
      <Tab.Pane eventKey="transfer"><Transfer walletName={walletName}/></Tab.Pane>
    </Tab.Content>
  </Tab.Container> 
  }
  
  export default TabController