import {
  Typography,
  Toolbar,
  ToolbarProps,
  Tooltip,
} from '@material-ui/core';
import { CheckCircle, HelpOutline, Cancel } from '@material-ui/icons';
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../app/rootReducer';
import { ebConnect } from '../utils/EventBusMiddleware';

const ConnectionStatus: React.FC<Exclude<ToolbarProps, ['children', 'container', 'item']>> = (props) => {
  const connection: 'open' | 'closed' = useSelector((state: RootState) => state.exec.connection);
  const connectionIcon: () => JSX.Element = () => {
    switch (connection) {
      case 'open':
        return <CheckCircle color="inherit" />;
      case 'closed':
        return <Cancel color="inherit" />;
      default:
        return <HelpOutline color="inherit" />;
    }
  };

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(ebConnect({ host: '/eventbus/' }));
  }, [connection, dispatch]);

  return (

    <Tooltip title={connection === 'open' ? 'Execution is running' : 'Run some code to connect to server'} arrow>
      <Toolbar {...props}>
        <Typography variant="h6">
          Connection status:&nbsp;
        </Typography>
        {connectionIcon()}
      </Toolbar>
    </Tooltip>
  );
};

export default ConnectionStatus;
