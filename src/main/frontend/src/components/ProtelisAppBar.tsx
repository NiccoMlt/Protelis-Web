import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Logo from '../styles/logo.png';

const useStyles = makeStyles((theme: Theme) => createStyles({
  root: {
    flexGrow: 1,
  },
  logo: {
    maxWidth: 32,
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
  },
}));

const ProtelisAppBar: React.FC = () => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar>
          <img src={Logo} alt="logo" className={classes.logo} />
          <Typography variant="h6" className={classes.title}>
            Protelis on the Web
          </Typography>
        </Toolbar>
      </AppBar>
    </div>
  );
};

export default ProtelisAppBar;
