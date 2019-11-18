import React, { useState } from 'react';
import Button from '@material-ui/core/Button';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog, { DialogProps } from '@material-ui/core/Dialog';
import { DialogContent, TextField, DialogActions } from '@material-ui/core';

type CreateFileProps = {
  name: string
  onDialogClose: (value: string | null) => void
};

export type CreateFileDialogProps = CreateFileProps & DialogProps & { open: boolean };

export const CreateFileDialog: React.FC<CreateFileDialogProps> = (props: CreateFileDialogProps) => {
  const {
    onDialogClose, name, open, ...dialogProps
  } = props;

  const [state, setState] = useState(name);

  const validState: () => boolean = () => !!state && state.trim() !== '';

  const accept = () => {
    onDialogClose(state);
    setState(name);
  };

  const close = () => {
    onDialogClose(null);
    setState(name);
  };

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setState(event.target.value.trim());
  };

  return (
    <Dialog onClose={close} aria-labelledby="create-file-dialog-title" open={open} {...dialogProps}>
      <DialogTitle id="create-file-dialog-title">Create new file</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="normal"
          id="name"
          label="File name"
          placeholder={name}
          type="text"
          variant="outlined"
          onChange={handleNameChange}
          fullWidth
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={close} color="primary">Cancel</Button>
        <Button onClick={() => (validState() ? accept() : close())} color="primary" disabled={!validState()}>Create</Button>
      </DialogActions>
    </Dialog>
  );
};

export default { CreateFileDialog };
