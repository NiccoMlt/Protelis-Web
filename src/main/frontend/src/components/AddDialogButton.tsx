import React, { useState } from 'react';
import {
  Button, ButtonProps,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
} from '@material-ui/core';
import { Dispatch } from 'redux';
import { useDispatch } from 'react-redux';
import { addFile } from '../features/editor/editorSlice';

type AddDialogProps = {
  open: boolean;
  error: string | false;
  handleClose: (name?: string) => void;
};

const AddDialog: React.FC<AddDialogProps> = (props: AddDialogProps) => {
  const { open, error, handleClose } = props;
  const [filename, setFileName] = useState('');

  const validState: () => boolean = () => !!filename && filename.trim() !== '';

  return (
    <Dialog open={open} onClose={() => handleClose()} aria-labelledby="form-dialog-title">
      <DialogTitle id="form-dialog-title">Create new file</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Add file name with extension:
        </DialogContentText>
        <TextField
          autoFocus
          margin="normal"
          id="name"
          label="File name"
          type="text"
          variant="outlined"
          onChange={(event) => setFileName(event.target.value.trim())}
          fullWidth
          error={!!error}
          helperText={error}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={() => handleClose()} color="primary">Cancel</Button>
        <Button
          onClick={() => (validState() ? handleClose(filename) : handleClose())}
          color="primary"
          disabled={!validState()}
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
};

type AddDialogButtonProps = Omit<ButtonProps, 'onClick'> & {
  validator: (name: string) => boolean;
};

export const AddDialogButton: React.FC<AddDialogButtonProps> = (props: AddDialogButtonProps) => {
  const [open, setOpen] = useState(false);
  const [error, setError] = useState<string | false>(false);

  const { validator, ...buttonProps } = props;

  const dispatch: Dispatch = useDispatch();

  const handleClickOpen = (): void => {
    setOpen(true);
  };

  const handleClose = (name?: string): void => {
    if (name) {
      if (validator(name)) {
        setOpen(false);
        dispatch(/* TODO: open dialog */ addFile({ name, content: '' }));
      } else {
        setError(`The file ${name} already exists`);
      }
    }
  };

  return (
    <>
      <Button onClick={handleClickOpen} {...buttonProps}>Add</Button>
      <AddDialog open={open} handleClose={handleClose} error={error} />
    </>
  );
};

export default AddDialogButton;
