import React, { ChangeEvent, useState } from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
} from '@material-ui/core';

type AddDialogProps = {
  open: boolean;
  error: string | false;
  handleClose: (name?: string) => void;
};

/** Dialog component that asks new file name. */
export const AddDialog: React.FC<AddDialogProps> = (props: AddDialogProps) => {
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
          onChange={(event: ChangeEvent<HTMLTextAreaElement>) => setFileName(event.target.value.trim())}
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

export default AddDialog;
