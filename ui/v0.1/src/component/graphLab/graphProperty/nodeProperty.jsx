import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../../css/structure.css';


export default class NodeProperty extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.setInfo = this.setInfo.bind(this);
        this.deleteProperty = this.deleteProperty.bind(this);
   }

   componentDidMount(){
        this.refs.propName.value = this.props.name;
        this.refs.propValue.value = this.props.value;
   }

   setInfo(name, value){
     this.refs.propName.value = name;
     this.refs.provValue.value = value;
   }

   deleteProperty(){
        this.props.deleteCallBack(this.props.indexKey);
   }

   render() {
        return <div className={styles.nodeProp}>
                    <span className={styles.nodePropDelete} onClick={this.deleteProperty}><img src="./../icon/mono_icons/minus32.png" className={styles.icon}/></span>
                    <input className={styles.nodePropName} ref="propName" type="text" />
                    <input className={styles.nodePropValue} ref="propValue" type="text" />
               </div>
   }
}