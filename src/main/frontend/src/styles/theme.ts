import { PaletteColor } from '@material-ui/core/styles/createPalette';
import { ThemeOptions } from '@material-ui/core/styles/createMuiTheme';

/** Protelis color palette generated from logo. */
export const protelisMain: PaletteColor = {
  light: '#98f7e3',
  main: '#65c4b1',
  dark: '#309382',
  contrastText: '#030404',
};

/** Theme options for Protelis color palette */
export const protelisTheme: ThemeOptions & { palette: { primary: PaletteColor } } = {
  palette: {
    primary: protelisMain,
  },
};
