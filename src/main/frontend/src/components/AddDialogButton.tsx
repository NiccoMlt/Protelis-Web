import React, { useState } from 'react';
import { Button, ButtonProps } from '@material-ui/core';
import { Dispatch } from 'redux';
import { useDispatch } from 'react-redux';
import { addFile } from '../features/editor/editorSlice';
import { AddDialog } from './AddDialog';

type AddDialogButtonProps = Omit<ButtonProps, 'onClick'> & {
  validator: (name: string) => boolean;
};

/** Button that adds a file opening a dialog to choose the file name. */
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
        dispatch(addFile({ name, content: 'hello world' }));
      } else {
        setError(`The file ${name} already exists`);
      }
    } else {
      setOpen(false);
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
