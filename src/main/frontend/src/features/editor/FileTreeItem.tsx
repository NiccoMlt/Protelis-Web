import React from 'react';
import Menu from '@material-ui/core/Menu';
import { withStyles } from '@material-ui/styles';
import { Theme, createStyles } from '@material-ui/core';
import { fade } from '@material-ui/core/styles';
import TreeItem, { TreeItemProps } from '@material-ui/lab/TreeItem';
import MenuItem from '@material-ui/core/MenuItem';

/** Model the position of the mouse cursor to open context menu. */
type MenuPosition = {
  mouseX: null | number,
  mouseY: null | number,
};

const initialState: MenuPosition = {
  mouseX: null,
  mouseY: null,
};

/** The FileTreeItem view component gets contained file path from props. */
type FileTreeItemProps = TreeItemProps & {
  /** The full path of the file in the item */
  filePath: string,
};

/** Wrap Material-UI TreeItem with CSS-in-JS style using an Higher-Order-Component. */
const StyledTreeItem = withStyles(
  (theme: Theme) => createStyles({
    iconContainer: {
      '& .close': {
        opacity: 0.3,
      },
    },
    group: {
      marginLeft: 12,
      paddingLeft: 12,
      borderLeft: `1px dashed ${fade(theme.palette.text.primary, 0.4)}`,
    },
  }),
)(
  (props: TreeItemProps) => <TreeItem {...props} />,
);

export const FileTreeItem: React.FC<FileTreeItemProps> = (props: FileTreeItemProps) => {
  const [state, setState] = React.useState<MenuPosition>(initialState);

  const handleClick = (event: React.MouseEvent<HTMLDivElement>) => {
    event.preventDefault();
    setState({
      mouseX: event.clientX - 2,
      mouseY: event.clientY - 4,
    });
  };

  const { filePath, ...treeItemProps } = props;

  const handleClose = () => {
    console.log(`${filePath} clicked`);
    setState(initialState);
  };

  return (
    <div onContextMenu={handleClick} style={{ cursor: 'context-menu' }}>
      <StyledTreeItem {...treeItemProps} />
      <Menu
        keepMounted
        open={state.mouseY !== null}
        onClose={handleClose}
        anchorReference="anchorPosition"
        anchorPosition={
          state.mouseY !== null && state.mouseX !== null
            ? { top: state.mouseY, left: state.mouseX }
            : undefined
        }
      >
        <MenuItem onClick={handleClose}>Rename</MenuItem>
        <MenuItem onClick={handleClose}>Delete</MenuItem>
      </Menu>
    </div>
  );
};

export default FileTreeItem;
