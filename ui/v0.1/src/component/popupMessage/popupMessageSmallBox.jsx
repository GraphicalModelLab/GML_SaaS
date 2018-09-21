import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';

export default class PopupMessageSmallBox extends React.Component {
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

    showMessage(message, timeout){
        this.setState({
            show: true,
            message: message,
            closeStyle: false
        });

        var self = this;
        setTimeout( function(message) {
            self.setState({
                show: false,
                message: "",
                closeStyle: false
            });
        }, timeout, message);
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
                    <div className={ this.state.closeStyle ? styles.popupMessageCloseSmallBox : styles.popupMessageSmallBox }>
                        {this.state.message}
                    </div>
                  ):(<div></div>)}
               </div>
    }
}