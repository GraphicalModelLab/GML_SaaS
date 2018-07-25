import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';

export default class PopupMessage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            show: false,
            message: "",
            closeStyle: false
        };
    }

    componentDidMount() {

    }

    showMessage(message){
        this.setState({
            show: true,
            message: message,
            closeStyle: false
        });
    }


    closeMessage(message){
        this.setState({
            show: true,
            message: message,
            closeStyle: true
        });
        var self = this;
        setTimeout( function(message) {
            self.setState({
                show: false,
                message: "",
                closeStyle: false
            });
        }, 500, message);
    }

    render() {
        return <div>
                  {this.state.show ? (
                    <div className={ this.state.closeStyle ? styles.popupMessageClose : styles.popupMessage }>
                        {this.state.message}
                    </div>
                  ):(<div></div>)}
               </div>
    }
}